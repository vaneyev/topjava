Get
```shell script
curl -i http://localhost:8080/topjava/rest/meals/100002
```
Get all
```shell script
curl -i http://localhost:8080/topjava/rest/meals
```
Delete
```shell script
curl -i -X DELETE http://localhost:8080/topjava/rest/meals/100002
```
Create
```shell script
curl --header "Content-Type: application/json" \
  --request POST \
  --data '{"id":"null","dateTime":"2020-02-01T18:00:00", "description":"Созданный ужин", "calories":"300"}' \
  http://localhost:8080/topjava/rest/meals
```
Update
```shell script
curl --header "Content-Type: application/json" \
  --request PUT \
  --data '{"id":"100002","dateTime":"2020-01-30T10:02:00", "description":"Обновленный завтрак", "calories":"200"}' \
  http://localhost:8080/topjava/rest/meals/100002
```
Get between
```shell script
curl -i "http://localhost:8080/topjava/rest/meals/between?startDate=2020-01-31&endDate=2020-01-31&startTime=00:10:00&endTime=23:59:59"
```
Get between with null
```shell script
curl -i http://localhost:8080/topjava/rest/meals/between
```
