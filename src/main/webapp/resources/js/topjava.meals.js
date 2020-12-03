var ctx;

// $(document).ready(function () {
$(function () {
    // https://stackoverflow.com/a/5064235/548473
    ctx = {
        ajaxUrl: "meals/",
        filterForm: $('#filterForm'),
        datatableApi: $("#datatable").DataTable({
            "paging": false,
            "info": true,
            "columns": [
                {
                    "data": "dateTime"
                },
                {
                    "data": "description"
                },
                {
                    "data": "calories"
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
                    "desc"
                ]
            ]
        })
    };
    $(".edit").click(function () {
        edit($(this).closest('tr').attr("id"))
    });
    makeEditable();
});

function resetFilter() {
    ctx.filterForm.trigger("reset");
    updateTable();
}

function edit(id) {
    form.find(":input#id").val(id);
    let url = ctx.ajaxUrl + id;
    $.get(url, function (data) {
        form.find(":input#dateTime").val(data.dateTime);
        form.find(":input#description").val(data.description);
        form.find(":input#calories").val(data.calories);
    });
    $("#editRow").modal();
}

