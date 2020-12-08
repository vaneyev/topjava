var ctx, mealAjaxUrl = "profile/meals/";

function updateFilteredTable() {
    $.ajax({
        type: "GET",
        url: "profile/meals/filter",
        data: $("#filter").serialize()
    }).done(updateTableByData);
}

function clearFilter() {
    $("#filter")[0].reset();
    $.get("profile/meals/", updateTableByData);
}

function setDateTimePicker() {
    let datePicker = {
        timepicker: false,
        format: 'Y-m-d'
    };
    $("#startDate").datetimepicker(datePicker);
    $("#endDate").datetimepicker(datePicker);
    let timePicker = {
        datepicker: false,
        format: 'H:m'
    };
    $("#startTime").datetimepicker(timePicker);
    $("#endTime").datetimepicker(timePicker);
    let dateTimePicker = {
        timepicker: false,
        format: 'Y-m-d\\TH:m',
    };
    $("#dateTime").datetimepicker(dateTimePicker);
}

$(function () {
    ctx = {
        ajaxUrl: mealAjaxUrl,
        datatableApi: $("#datatable").DataTable({
            "ajax": {
                "url": mealAjaxUrl,
                "dataSrc": ""
            },
            "paging": false,
            "info": true,
            "columns": [
                {
                    "data": "dateTime",
                    "render": function (data, type, row) {
                        if (type === "display") {
                            return data.replace("T", " ").substring(0, 16);
                        }
                        return data;
                    }
                },
                {
                    "data": "description"
                },
                {
                    "data": "calories"
                },
                {
                    "defaultContent": "Edit",
                    "orderable": false,
                    "render": renderEditBtn
                },
                {
                    "defaultContent": "Delete",
                    "orderable": false,
                    "render": renderDeleteBtn
                }
            ],
            "order": [
                [
                    0,
                    "desc"
                ]
            ],
            "createdRow": function (row, data, dataIndex) {
                $(row).attr("data-mealExcess", data.excess);
            }
        }),
        updateTable: updateFilteredTable
    };
    makeEditable();
    setDateTimePicker();
});
