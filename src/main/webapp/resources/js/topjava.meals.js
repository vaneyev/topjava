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
    $("#startDate").datetimepicker(ctx.startDatePicker);
    $("#endDate").datetimepicker(ctx.endDatePicker);
    $("#startTime").datetimepicker(ctx.startTimePicker);
    $("#endTime").datetimepicker(ctx.endTimePicker);
    let dateTimePicker = {
        format: 'Y-m-d\\TH:m',
    };
    $("#dateTime").datetimepicker(dateTimePicker);
}

$(function () {
    ctx = {
        startDatePicker: {
            timepicker: false,
            format: 'Y-m-d',
            formatDate: 'Y-m-d',
            onSelectDate: function ($dtp, current, input) {
                ctx.endDatePicker.minDate = $dtp.toLocaleDateString("fr-CA");
                $("#endDate").datetimepicker(ctx.endDatePicker);
            },
        },
        endDatePicker: {
            timepicker: false,
            format: 'Y-m-d',
            formatDate: 'Y-m-d',
            onSelectDate: function ($dtp, current, input) {
                ctx.startDatePicker.maxDate = $dtp.toLocaleDateString("fr-CA");
                $("#startDate").datetimepicker(ctx.startDatePicker);
            },
        },
        startTimePicker: {
            datepicker: false,
            format: 'H:i',
            formatTime:'H:i',
            onSelectTime: function ($dtp, current, input) {
                ctx.endTimePicker.minTime = $dtp.toLocaleTimeString();
                $("#endTime").datetimepicker(ctx.endTimePicker);
            },
        },
        endTimePicker: {
            datepicker: false,
            format: 'H:i',
            formatTime:'H:i',
            onSelectTime: function ($dtp, current, input) {
                ctx.startTimePicker.maxTime = $dtp.toLocaleTimeString();
                $("#startTime").datetimepicker(ctx.startTimePicker);
            },
        },
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
