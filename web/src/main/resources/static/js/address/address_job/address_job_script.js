let addressJobsTable = createAddressJobsTable();

//-----------------------------------------------------------------------------------

//Добавляем таблицы------------------------------------
function createAddressJobsTable(){
    return new Tabulator("#address-jobs-table",{
        ajaxURL: "/tables/address/address_job_table",
        maxHeight: "80%",
        selectableRows: true,
        movableColumns: true,
        addRowPos: "top",
        pagination: "local",
        paginationSize: 10,
        paginationSizeSelector: [10, 20, true],
        paginationCounter: "rows",
        layout: "fitDataStretch",
        rowContextMenu: createResponsibleMenu(),
        columns: [
            {title:"Id", field: "id"},
            {title: "Имя", field: "name"},
            {title: "Список профессий на объекте",field: "jobs"},
        ]
    })
}

function createResponsibleMenu(){
    return [
        {
            label: "<i class='fas fa-user'></i> Изменить список профессий",
            action: function (e, row) {
                editJobsOnAddress(e,row)
            }
        }
    ];
}


//_________________________________________________________________
//Смотреть все специальности кнопка
document.getElementById("check-all-jobs")
    .addEventListener("click",function () {
        showJobs()
    });

document.getElementById("all-jobs-exit")
    .addEventListener("click",function () {
        closeShowJobs()
    })

