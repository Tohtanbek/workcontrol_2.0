let dragJobsTable;
let jobsOfAddressTable;
createDragJobsTable();
createJobsOfAddressTable();

let chosenRow;

function createDragJobsTable(){
    dragJobsTable = new Tabulator("#drag-jobs-table",{
        data: [],
        layout: "fitDataStretch",
        maxHeight: "80%",
        movableRows: true,
        movableRowsConnectedTables: "#jobs-of-address-table",
        movableRowsSender: "delete",
        movableRowsReceiver: "add",
        columns: [
            {title: "Id", field: "id"},
            {title: "Название", field: "name"},
            {title: "Коэффициент ЗП", field: "wageRate"},
            {title: "Коэффициент дохода", field: "incomeRate"},
            {title: "Почасово",field: "isHourly"}
        ]
    })
}
function createJobsOfAddressTable(){
    jobsOfAddressTable = new Tabulator("#jobs-of-address-table",{
        data: [],
        layout: "fitDataStretch",
        maxHeight: "80%",
        movableRows: true,
        movableRowsConnectedTables: "#drag-jobs-table",
        movableRowsSender: "delete",
        movableRowsReceiver: "add",
        columns: [
            {title: "Id", field: "id"},
            {title: "Название", field: "name"},
            {title: "Коэффициент ЗП", field: "wageRate"},
            {title: "Коэффициент дохода", field: "incomeRate"},
            {title: "Почасово",field: "isHourly"}
        ]
    })
}

//кнопка меню редактировать список профессий
function editJobsOnAddress(e,row){
    chosenRow = row;
    jobsOfAddressTable.setData("/tables/job/load_jobs_by_address_id?id="+row.getData().id)
    dragJobsTable.setData("/tables/job/load_jobs_for_address?id="+row.getData().id)

    let upperDragMenu = document.querySelector("#upper-drag-menu");
    let dragJobsTableEl = document.querySelector("#drag-jobs-table");
    let addressJobsTableEl = document.querySelector("#jobs-of-address-table");

    upperDragMenu.style.visibility = "visible"
    upperDragMenu.style.opacity = "1"
    dragJobsTableEl.style.visibility = "visible";
    dragJobsTableEl.style.opacity = "1";
    addressJobsTableEl.style.visibility = "visible";
    addressJobsTableEl.style.opacity = "1";

    let tableDiv = document.querySelector("#drag-jobs-box");
    tableDiv.style.opacity = "1";
    tableDiv.style.visibility = "visible";
    let main = document.querySelector("main");
    main.style.opacity = "0.5";
    main.style.filter = "blur(5px)";
    document.body.style.overflow = "hidden"; //Убрали возможность скролить после нажатия
    document.getElementById("overlay").style.display = "block";
}

//Крестик (закрыть выбор специальностей)
document.getElementById("drag-exit").addEventListener("click",function (){
    closeJobsEditor();
})

//Слушатель кнопки сохранить изменения специальностей на объекте.
document.querySelector("#save-jobs-on-address")
    .addEventListener("click",async function (){
        let chosenRowId = chosenRow.getData().id;
        let actualAddressJobs = jobsOfAddressTable.getData();

        let response =
            await fetch("/tables/address/change_jobs_on_address?id="+chosenRowId,{
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(actualAddressJobs)
        });
        if (!response.ok){
            closeJobsEditor();
            addressJobsTable.alert("Ошибка при изменении списка профессий на объекте","error");
            setTimeout(function (){addressJobsTable.clearAlert()},3000);
            throw Error("error loading the update of jobs")
        }
        else {
            addressJobsTable.setData("/tables/address/address_job_table").then(function (){
                closeJobsEditor()
                addressJobsTable.alert("Успешно загрузили изменения");
                setTimeout(function (){addressJobsTable.clearAlert()},3000);
            })
        }
    })


function closeJobsEditor(){
    let upperDragMenu = document.querySelector("#upper-drag-menu");
    let dragJobsTable = document.querySelector("#drag-jobs-table");
    let addressJobsTable = document.querySelector("#jobs-of-address-table");

    upperDragMenu.style.visibility = "hidden"
    upperDragMenu.style.opacity = "0"
    dragJobsTable.style.visibility = "hidden";
    dragJobsTable.style.opacity = "0";
    addressJobsTable.style.visibility = "hidden";
    addressJobsTable.style.opacity = "0";

    let tableDiv = document.querySelector("#drag-jobs-box");
    tableDiv.style.opacity = "0";
    tableDiv.style.visibility = "hidden";
    let main = document.querySelector("main");
    main.style.opacity = "1";
    main.style.filter = "blur(0px)";
    document.body.style.overflow = "visible"; //Убрали возможность скролить после нажатия
    document.getElementById("overlay").style.display = "none";

    chosenRow = null;
}



//-----------------------------------------------------------------------
