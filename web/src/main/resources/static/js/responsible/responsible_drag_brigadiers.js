let dragBrigadiersTable;
let responsibleBrigadiersTable;
createDragBrigadiersTable();
createResponsibleBrigadiersTable();

let chosenRow;

function createDragBrigadiersTable(){
    dragBrigadiersTable = new Tabulator("#dragBrigadiersTable",{
        data: [],
        layout: "fitDataStretch",
        maxHeight: "80%",
        movableRows: true,
        movableRowsConnectedTables: "#responsibleBrigadiersTable",
        movableRowsSender: "delete",
        movableRowsReceiver: "add",
        columns: [
            {title: "Id", field: "id"},
            {title: "Имя", field: "name"},
            {title: "Телефон", field: "phoneNumber"}
        ]
    })
}
function createResponsibleBrigadiersTable(){
    responsibleBrigadiersTable = new Tabulator("#responsibleBrigadiersTable",{
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
function editBrigadiersOfSupervisorRow(e,row){
    chosenRow = row;
    responsibleBrigadiersTable.setData("/tables/brigadier/load_brigadiers_by_supervisor_id?id="+row.getData().id)
    dragBrigadiersTable.setData("/tables/brigadier/load_brigadier_for_supervisor?id="+row.getData().id)

    let upperDragMenu = document.querySelector("#upper-drag-menu");
    let dragBrigadiersTableEl = document.querySelector("#dragBrigadiersTable");
    let superBrigadiersTableEl = document.querySelector("#responsibleBrigadiersTable");

    upperDragMenu.style.visibility = "visible"
    upperDragMenu.style.opacity = "1"
    dragBrigadiersTableEl.style.visibility = "visible";
    dragBrigadiersTableEl.style.opacity = "1";
    superBrigadiersTableEl.style.visibility = "visible";
    superBrigadiersTableEl.style.opacity = "1";

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
document.querySelector("#save-brigadiers-on-supervisor")
    .addEventListener("click",async function (){
        let chosenRowId = chosenRow.getData().id;
        let actualBrigadiersSupervisor = responsibleBrigadiersTable.getData();

        let response =
            await fetch("/tables/supervisors/change_brigadiers_on_supervisor?id="+chosenRowId,{
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(actualBrigadiersSupervisor)
        });
        if (!response.ok){
            closeBrigadierEditor();
            responsibleTable.alert("Ошибка при изменении списка бригадиров на объекте","error");
            setTimeout(function (){responsibleTable.clearAlert()},3000);
            throw Error("error loading the update of brigadiers")
        }
        else {
            responsibleTable.setData("/tables/supervisors/main_table").then(function (){
                loadLinkedBrigadiers()
                closeBrigadierEditor()
                responsibleTable.alert("Успешно загрузили изменения");
                setTimeout(function (){responsibleTable.clearAlert()},3000);
            })
        }
    })


function closeBrigadierEditor(){
    let upperDragMenu = document.querySelector("#upper-drag-menu");
    let dragBrigadiersTable = document.querySelector("#dragBrigadiersTable");
    let responsibleBrigadiersTable = document.querySelector("#responsibleBrigadiersTable");

    upperDragMenu.style.visibility = "hidden"
    upperDragMenu.style.opacity = "0"
    dragBrigadiersTable.style.visibility = "hidden";
    dragBrigadiersTable.style.opacity = "0";
    responsibleBrigadiersTable.style.visibility = "hidden";
    responsibleBrigadiersTable.style.opacity = "0";

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
