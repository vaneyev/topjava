package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Transactional(readOnly = true)
public class JdbcUserRepository implements UserRepository {

    private static final BeanPropertyRowMapper<User> ROW_MAPPER = BeanPropertyRowMapper.newInstance(User.class);

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    @Transactional
    public User save(User user) {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (violations.size() > 0) {
            throw new ConstraintViolationException(violations);
        }

        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);

        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
        } else if (namedParameterJdbcTemplate.update("""
                   UPDATE users SET name=:name, email=:email, password=:password, 
                   registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id
                """, parameterSource) == 0) {
            return null;
        } else {
            jdbcTemplate.update("DELETE FROM user_roles WHERE user_id=?", user.getId());
        }
        List<Role> roles = List.copyOf(user.getRoles());
        if (roles.isEmpty()) {
            return user;
        }
        jdbcTemplate.batchUpdate(
                "INSERT INTO user_roles (user_id, role) VALUES (?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                        preparedStatement.setInt(1, user.getId());
                        preparedStatement.setString(2, roles.get(i).toString());
                    }

                    @Override
                    public int getBatchSize() {
                        return user.getRoles().size();
                    }
                });
        return user;
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE id=?", ROW_MAPPER, id);
        if (!users.isEmpty()) {
            users.get(0).setRoles(getRolesByUserId(users.get(0).id()));
        }
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public User getByEmail(String email) {
//        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE email=?", ROW_MAPPER, email);
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE email=?", ROW_MAPPER, email);
        if (!users.isEmpty()) {
            users.get(0).setRoles(getRolesByUserId(users.get(0).id()));
        }
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public List<User> getAll() {
        Map<Integer, List<Role>> roles = jdbcTemplate.queryForStream("SELECT * FROM user_roles",
                (resultSet, i) -> Map.entry(resultSet.getInt("user_id"), Role.valueOf(resultSet.getString("role"))))
                .collect(Collectors.groupingBy(Map.Entry::getKey, Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
        return jdbcTemplate.queryForStream("SELECT * FROM users ORDER BY name, email", ROW_MAPPER)
                .peek(user -> user.setRoles(roles.get(user.getId())))
                .collect(Collectors.toList());
    }

    private List<Role> getRolesByUserId(int id) {
        return jdbcTemplate.query(
                "SELECT role FROM user_roles WHERE user_id = ?",
                (resultSet, i) -> Role.valueOf(resultSet.getString("role")),
                id);
    }
}
