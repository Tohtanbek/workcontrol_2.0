function createDragBrigadiersTable(){
    return new Tabulator("#dragBrigadiersTable",{
        ajaxURL:"/tables/brigadier/load_brigadier_small_table",
        layout: "fitDataStretch",
        maxHeight: "80%",
        columns: [
            {title: "Id", field: "id"},
            {title: "Имя", field: "name"},
            {title: "Телефон", field: "phoneNumber"}
        ]
    })
}
function createAddressBrigadiersTable(addressId){
    return new Tabulator("#addressBrigadiersTable",{
        ajaxURL: "/tables/brigadier/load_brigadiers_by_address_id?id="+addressId,
        layout: "fitDataStretch",
        maxHeight: "80%",
        columns: [
            {title: "Id", field: "id"},
            {title: "Имя", field: "name"},
            {title: "Телефон", field: "phoneNumber"}
        ]
    })
}

//кнопка меню редактировать бригадиров
function editBrigadiersOfAddressRow(e,row){
    createAddressBrigadiersTable(row.getData().id)

    let dragBrigadiersTable = document.querySelector("#dragBrigadiersTable");
    let addressBrigadiersTable = document.querySelector("#addressBrigadiersTable");

    dragBrigadiersTable.style.visibility = "visible";
    dragBrigadiersTable.style.opacity = "1";
    addressBrigadiersTable.style.visibility = "visible";
    addressBrigadiersTable.style.opacity = "1";

    let tableDiv = document.querySelector("#drag-brigadiers-box");
    tableDiv.style.opacity = "1";
    tableDiv.style.visibility = "visible";
    let main = document.querySelector("main");
    main.style.opacity = "0.5";
    main.style.filter = "blur(5px)";
    document.body.style.overflow = "hidden"; //Убрали возможность скролить после нажатия
    document.getElementById("overlay").style.display = "block";
}