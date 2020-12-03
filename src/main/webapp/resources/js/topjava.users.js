var ctx;

// $(document).ready(function () {
$(function () {
    // https://stackoverflow.com/a/5064235/548473
    ctx = {
        ajaxUrl: "admin/users/",
        datatableApi: $("#datatable").DataTable({
            "paging": false,
            "info": true,
            "columns": [
                {
                    "data": "name"
                },
                {
                    "data": "email"
                },
                {
                    "data": "roles"
                },
                {
                    "data": "enabled"
                },
                {
                    "data": "registered"
                },
                {
                    "defaultContent": "Edit",
                    "orderable": false
                },
                {
                    "defaultContent": "Delete",
                    "orderable": false
                }
            ],
            "order": [
                [
                    0,
                    "asc"
                ]
            ]
        })
    };
    $(":input[name='enabled-user']:not(:checked)").closest('tr').attr("style", "background-color: lightgray");
    $(":input[name='enabled-user']").click(function () {
        enableUser($(this).prop("checked"), $(this).closest('tr'))
    });

    makeEditable();
});

function enableUser(enabled, tr) {
    let data = new URLSearchParams();
    data.append("id", tr.attr("id"))
    data.append("enabled", enabled)
    $.ajax({
        type: "POST",
        url: ctx.ajaxUrl + "enable",
        data: data.toString(),
        success: function () {
            if (enabled) {
                tr.attr("style", "")
            } else {
                tr.attr("style", "background-color: lightgray")
            }
        }
    });
}