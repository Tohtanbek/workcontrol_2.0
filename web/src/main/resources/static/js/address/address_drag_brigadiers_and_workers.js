let dragBrigadiersTable;
let addressBrigadiersTable;
createDragBrigadiersTable();
createAddressBrigadiersTable();
let dragWorkersTable;
let addressWorkersTable;
createDragWorkersTable();
createAddressWorkersTable();

let chosenRow;

function createDragBrigadiersTable(){
    dragBrigadiersTable = new Tabulator("#dragBrigadiersTable",{
        data: [],
        layout: "fitDataStretch",
        maxHeight: "80%",
        movableRows: true,
        movableRowsConnectedTables: "#addressBrigadiersTable",
        movableRowsSender: "delete",
        movableRowsReceiver: "add",
        columns: [
            {title: "Id", field: "id"},
            {title: "Имя", field: "name"},
            {title: "Телефон", field: "phoneNumber"}
        ]
    })
}
function createAddressBrigadiersTable(){
    addressBrigadiersTable = new Tabulator("#addressBrigadiersTable",{
        data: [],
        layout: "fitDataStretch",
        maxHeight: "80%",
        movableRows: true,
        movableRowsConnectedTables: "#dragBrigadiersTable",
        movableRowsSender: "delete",
        movableRowsReceiver: "add",
        columns: [
            {title: "Id", field: "id"},
            {title: "Имя", field: "name"},
            {title: "Телефон", field: "phoneNumber"}
        ]
    })
}

//кнопка меню редактировать бригадиров
function editBrigadiersOfAddressRow(e,row){
    chosenRow = row;
    addressBrigadiersTable.setData("/tables/brigadier/load_brigadiers_by_address_id?id="+row.getData().id)
    dragBrigadiersTable.setData("/tables/brigadier/load_brigadier_small_table?id="+row.getData().id)

    let upperDragMenu = document.querySelector("#upper-drag-menu");
    let lowerDragMenu = document.querySelector("#lower-drag-menu");
    let dragBrigadiersTableEl = document.querySelector("#dragBrigadiersTable");
    let addressBrigadiersTableEl = document.querySelector("#addressBrigadiersTable");

    upperDragMenu.style.visibility = "visible"
    upperDragMenu.style.opacity = "1"
    lowerDragMenu.style.visibility = "visible"
    lowerDragMenu.style.opacity = "1"
    dragBrigadiersTableEl.style.visibility = "visible";
    dragBrigadiersTableEl.style.opacity = "1";
    addressBrigadiersTableEl.style.visibility = "visible";
    addressBrigadiersTableEl.style.opacity = "1";

    let tableDiv = document.querySelector("#drag-brigadiers-box");
    tableDiv.style.opacity = "1";
    tableDiv.style.visibility = "visible";
    let main = document.querySelector("main");
    main.style.opacity = "0.5";
    main.style.filter = "blur(5px)";
    document.body.style.overflow = "hidden"; //Убрали возможность скролить после нажатия
    document.getElementById("overlay").style.display = "block";
}

//Крестик (закрыть бригадиров)
document.getElementById("drag-exit").addEventListener("click",function (){
    closeBrigadierEditor();
})

//Слушатель кнопки сохранить изменения бригадиров.
document.querySelector("#save-brigadiers-on-address")
    .addEventListener("click",async function (){
        let chosenRowId = chosenRow.getData().id;
        let actualBrigadiersOnAddress = addressBrigadiersTable.getData();

        let response = await fetch("/tables/address/change_brigadiers_on_address?id="+chosenRowId,{
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(actualBrigadiersOnAddress)
        });
        if (!response.ok){
            closeBrigadierEditor();
            addressTable.alert("Ошибка при изменении списка бригадиров на объекте","error");
            setTimeout(function (){addressTable.clearAlert()},3000);
            throw Error("error loading the update of brigadiers")
        }
        else {
            addressTable.setData("/tables/address/main_table").then(function (){
                closeBrigadierEditor()
                addressTable.alert("Успешно загрузили изменения");
                setTimeout(function (){addressTable.clearAlert()},3000);
            })
        }
    })


function closeBrigadierEditor(){
    let upperDragMenu = document.querySelector("#upper-drag-menu");
    let lowerDragMenu = document.querySelector("#lower-drag-menu");
    let dragBrigadiersTable = document.querySelector("#dragBrigadiersTable");
    let addressBrigadiersTable = document.querySelector("#addressBrigadiersTable");

    upperDragMenu.style.visibility = "hidden"
    upperDragMenu.style.opacity = "0"
    lowerDragMenu.style.visibility = "hidden"
    lowerDragMenu.style.opacity = "0"
    dragBrigadiersTable.style.visibility = "hidden";
    dragBrigadiersTable.style.opacity = "0";
    addressBrigadiersTable.style.visibility = "hidden";
    addressBrigadiersTable.style.opacity = "0";

    let tableDiv = document.querySelector("#drag-brigadiers-box");
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
//Работники

function createDragWorkersTable(){
    dragWorkersTable = new Tabulator("#dragWorkersTable",{
        data: [],
        layout: "fitDataStretch",
        maxHeight: "80%",
        movableRows: true,
        movableRowsConnectedTables: "#addressWorkersTable",
        movableRowsSender: "delete",
        movableRowsReceiver: "add",
        columns: [
            {title: "Id", field: "id"},
            {title: "Имя", field: "name"},
            {title: "Телефон", field: "phoneNumber"}
        ]
    })
}
function createAddressWorkersTable(){
    addressWorkersTable = new Tabulator("#addressWorkersTable",{
        data: [],
        layout: "fitDataStretch",
        maxHeight: "80%",
        movableRows: true,
        movableRowsConnectedTables: "#dragWorkersTable",
        movableRowsSender: "delete",
        movableRowsReceiver: "add",
        columns: [
            {title: "Id", field: "id"},
            {title: "Имя", field: "name"},
            {title: "Телефон", field: "phoneNumber"}
        ]
    })
}

//кнопка меню редактировать бригадиров
function editWorkersOfAddressRow(e,row){
    chosenRow = row;
    addressWorkersTable.setData("/tables/worker/load_workers_by_address_id?id="+row.getData().id)
    dragWorkersTable.setData("/tables/worker/load_workers_small_table?id="+row.getData().id)

    let upperDragMenu = document.querySelector("#upper-drag-menu-workers");
    let lowerDragMenu = document.querySelector("#lower-drag-menu");
    let dragWorkersTableEl = document.querySelector("#dragWorkersTable");
    let addressWorkersTableEl = document.querySelector("#addressWorkersTable");

    upperDragMenu.style.visibility = "visible"
    upperDragMenu.style.opacity = "1"
    lowerDragMenu.style.visibility = "visible"
    lowerDragMenu.style.opacity = "1"
    dragWorkersTableEl.style.visibility = "visible";
    dragWorkersTableEl.style.opacity = "1";
    addressWorkersTableEl.style.visibility = "visible";
    addressWorkersTableEl.style.opacity = "1";

    let tableDiv = document.querySelector("#drag-workers-box");
    tableDiv.style.opacity = "1";
    tableDiv.style.visibility = "visible";
    let main = document.querySelector("main");
    main.style.opacity = "0.5";
    main.style.filter = "blur(5px)";
    document.body.style.overflow = "hidden"; //Убрали возможность скролить после нажатия
    document.getElementById("overlay").style.display = "block";
}

//Крестик (закрыть работников)
document.getElementById("drag-exit-workers").addEventListener("click",function (){
    closeWorkerEditor();
})

//Слушатель кнопки сохранить изменения бригадиров.
document.querySelector("#save-workers-on-address")
    .addEventListener("click",async function (){
        let chosenRowId = chosenRow.getData().id;
        let actualWorkersOnAddress = addressWorkersTable.getData();

        let response = await fetch("/tables/address/change_workers_on_address?id="+chosenRowId,{
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(actualWorkersOnAddress)
        });
        if (!response.ok){
            closeWorkerEditor();
            addressTable.alert("Ошибка при изменении списка работников на объекте","error");
            setTimeout(function (){addressTable.clearAlert()},3000);
            throw Error("error loading the update of workers")
        }
        else {
            addressTable.setData("/tables/address/main_table").then(function (){
                closeWorkerEditor()
                addressTable.alert("Успешно загрузили изменения");
                setTimeout(function (){addressTable.clearAlert()},3000);
            })
        }
    })


function closeWorkerEditor(){
    let upperDragMenu = document.querySelector("#upper-drag-menu-workers");
    let lowerDragMenu = document.querySelector("#lower-drag-menu");
    let dragWorkersTable = document.querySelector("#dragWorkersTable");
    let addressWorkersTable = document.querySelector("#addressWorkersTable");

    upperDragMenu.style.visibility = "hidden"
    upperDragMenu.style.opacity = "0"
    lowerDragMenu.style.visibility = "hidden"
    lowerDragMenu.style.opacity = "0"
    dragWorkersTable.style.visibility = "hidden";
    dragWorkersTable.style.opacity = "0";
    addressWorkersTable.style.visibility = "hidden";
    addressWorkersTable.style.opacity = "0";

    let tableDiv = document.querySelector("#drag-workers-box");
    tableDiv.style.opacity = "0";
    tableDiv.style.visibility = "hidden";
    let main = document.querySelector("main");
    main.style.opacity = "1";
    main.style.filter = "blur(0px)";
    document.body.style.overflow = "visible"; //Убрали возможность скролить после нажатия
    document.getElementById("overlay").style.display = "none";

    chosenRow = null;
}