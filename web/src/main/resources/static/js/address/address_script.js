let updatedRows = [];
let addressMenu = createAddressMenu();
let addressTable = createAddressTable();
let excelAddressTable = createExcelAddressTable();
//Переход к нопке добавить ряд-----------------------------------------------------
//кнопка "добавить ряд" (выводит форму для нового ряда)
document.getElementById("add-row-button").addEventListener("click",async function (){
    let addressForm = document.querySelector(".address-form");
    addressForm.style.display = "block";
    setTimeout(() => {
        addressForm.style.opacity = "1";
    }, 50);
    let main = document.querySelector("main");
    main.style.opacity = "0.5";
    main.style.filter = "blur(5px)";
    document.body.style.overflow = "hidden"; //Убрали возможность скролить после нажатия
    document.getElementById("overlay").style.display = "block";

    //Получаем бригадиров в форму  (но сначала очищаем, чтобы не дублировались):
    let brigadierCheckBoxDiv = document.querySelector("#brigadiers");
    while (brigadierCheckBoxDiv.firstChild) {
        brigadierCheckBoxDiv.removeChild(brigadierCheckBoxDiv.firstChild);
    }
    let jsonBrigadierMap;
    loadBrigadierJsonMap().then(json => {
        jsonBrigadierMap = json;
        //Добавляем варианты чекбокса бригадиров (сначала загружаем через api)
        for (let entry in jsonBrigadierMap){
            let freshVariant = document.createElement("INPUT");
            freshVariant.setAttribute("type","checkbox");
            freshVariant.setAttribute("name",jsonBrigadierMap[entry]);
            freshVariant.setAttribute("value",entry);
            let label = document.createElement("span");
            label.textContent = jsonBrigadierMap[entry];

            brigadierCheckBoxDiv.appendChild(label);
            brigadierCheckBoxDiv.appendChild(freshVariant)
        }
    });
//Получаем работников в форму
    let workerCheckBoxDiv = document.querySelector("#workers");
    while (workerCheckBoxDiv.firstChild) {
        workerCheckBoxDiv.removeChild(workerCheckBoxDiv.firstChild);
    }
    let jsonWorkerMap;
    loadWorkerJsonMap().then(json => {
        jsonWorkerMap = json
        //Добавляем варианты чекбокса бригадиров (сначала загружаем через api)
        for (let entry in jsonWorkerMap){
            let freshVariant = document.createElement("INPUT");
            freshVariant.setAttribute("type","checkbox");
            freshVariant.setAttribute("name",jsonWorkerMap[entry]);
            freshVariant.setAttribute("value",entry);
            let label = document.createElement("span");
            label.textContent = jsonWorkerMap[entry];

            workerCheckBoxDiv.appendChild(label);
            workerCheckBoxDiv.appendChild(freshVariant)
        }
    });
})
//Отправляем форму и ожидаем ответа, чтобы обновить таблицу, не обновляя всю страницу
document.getElementById("main-form-submit").addEventListener("click",
    async function (event){
        event.preventDefault();
        let formJson = JSON.stringify(createFormJson());
        console.log(formJson)
        try {
            let response = await fetch('/tables/address/add_address_row', {
                method: "POST",
                headers: {
                    "Content-Type":"application/json"
                },
                body: formJson
            });
            if (!response.ok) {
                throw new Error('Ошибка при отправке формы');
            }
            else {
                console.log("Форма на создание адреса создана успешно");
                addressTable.setData("/tables/address/main_table");
                $('#form-popup').addClass('is-visible');
            }
        } catch (error){
            console.error('Ошибка при отправке формы', error);
        }
    })
//Крестик (закрыть форму)
document.getElementById("form-exit").addEventListener("click",function (){
    closeForm()
})
//-----------------------------------------------------------------------------------


//---------------------------------------------------------------------

//Таблица адресов-------------------------------------------------


//Сама таблица адресов
function createAddressTable() {
    return new Tabulator("#address-table", {
        ajaxURL: "/tables/address/main_table",
        maxHeight: "80%",
        selectableRows: true,
        movableColumns: true,
        addRowPos: "top",
        pagination: "local",
        paginationSize: 10,
        paginationSizeSelector: [10, 20, 30, 50, 100, true],
        paginationCounter: "rows",
        layout: "fitData",
        initialSort:[
            {column:"id", dir:"desc"}, //sort by this first
        ],
        rowContextMenu: addressMenu,
        columns: [
            {title: "Id", field: "id", sorter: "number"},
            {title: "Название", field: "shortName", editor: true},
            {title: "Полный адрес", field: "fullName", editor: true},
            {title: "Зона", field: "zone",editor: true},
            {title: "Бригадиры", field: "brigadiers"},
            {title: "Персонал", field: "workers"}
        ]
    });
}

function createAddressMenu(){
    return [
        {
            label: "<i class='fas fa-user'></i>Удалить выбранные ряды",
            action: function (e, row) {
                $('#address-delete-popup').addClass('is-visible');
            }
        },
        {
            label: "<i class='fas fa-user'></i> Изменить список работников",
            action: function (e, row) {
                editWorkersOfAddressRow(e,row)
            }
        },
        {
            label: "<i class='fas fa-user'></i> Изменить список бригадиров",
            action: function (e, row) {
                editBrigadiersOfAddressRow(e,row)
            }
        }
    ];
}

//Фильтры для основной таблицы
let filterColumn = document.getElementById("filter-column");
let filterInput = document.getElementById("filter-input");
let filterClearButton = document.getElementById("clear-filter");
filterColumn.addEventListener("change",updateFilter);
filterInput.addEventListener("keyup",updateFilter);

function updateFilter(){
    let filterColumnValue = filterColumn.options[filterColumn.selectedIndex].value;
    addressTable.setFilter(filterColumnValue,"like",filterInput.value);

    filterClearButton.addEventListener("click",function (){
        filterColumn.value = "";
        filterInput.value = "";
        addressTable.clearFilter();
    })
}

function closeForm(){
    let addressForm = document.querySelector(".address-form");
    addressForm.style.opacity = "0";
    setTimeout(function (){addressForm.style.display = "none"},300);//Чтобы была анимация
    let main = document.querySelector("main");
    main.style.opacity = "1";
    main.style.filter = "blur(0px)";
    document.body.style.overflow = "visible"; //Вернули возможность скролить после нажатия
    document.getElementById("overlay").style.display = "none";
}


//При нажатии кнопки коллекция с измененными dto отправляется на сервер и очищается
document.querySelector("#main-table-save-update")
    .addEventListener("click",function () {
    fetch("/tables/address/update_address_rows",{
        method: "PUT",
        headers:{
            "Content-Type":"application/json"
        },
        body: JSON.stringify(updatedRows)
    }).then(response => {
        if (!response.ok){
            updatedRows = [];
            addressTable.alert("Ошибка. Изменения не сохранены","error");
            setTimeout(function (){
                addressTable.clearAlert();
            },3000)
            throw new Error('DB error')
        }
        else {
            updatedRows = [];
            addressTable.alert("Изменения сохранены успешно");
            setTimeout(function (){
                addressTable.clearAlert();
            },2000)
        }
    })
})

//Слушатель обновления рядов в основной таблице. Сохраняет ряды, в которых внесены изменения
addressTable.on("cellEdited",function (cell){
    let row = cell.getRow().getData();
    console.log(JSON.stringify(row));
    for (let i=0; i<updatedRows.length;i++){
        let prevUpdRow = updatedRows[i];
        if (prevUpdRow.id === row.id){
            updatedRows.splice(i,1);
            break;
        }
    }
    updatedRows.push(row);
    console.log(updatedRows);
    console.log(JSON.stringify(updatedRows));
})

// Метод загрузки бригадиров для формы
async function loadBrigadierJsonMap() {
    try {
        let response = await fetch("/tables/brigadier/load_brigadier_map", {
            method: "GET"
        });
        if (!response.ok) {
            throw new Error("Internal Server Error");
        }
        return await response.json();
    } catch (error) {
        console.error("Error while fetching brigadier map:", error);
        throw error;
    }
}
//метод загрузки работников для формы
async function loadWorkerJsonMap(){
    try {
        let response = await fetch("/tables/worker/load_worker_map", {
            method: "GET"
        })
        if (!response.ok){
            throw new Error("internal server error");
        }
        return await response.json();
    } catch (error) {
        console.error("Error while fetching worker map:", error);
        throw error;
    }
}

//Создает корректный json формы на отправку
function createFormJson(){

    let shortName = document.querySelector("#shortNaming").value;
    let fullName = document.querySelector("#fullNaming").value;
    let zone = document.querySelector("#zone").value;

    let selectedBrigadiers = document.querySelectorAll('#brigadiers input:checked');
    let selectedWorkers = document.querySelectorAll('#workers input:checked');

    let selectedBrigadiersArray = Array.from(selectedBrigadiers).map(input => input.name);
    let selectedWorkersArray = Array.from(selectedWorkers).map(input => input.name);

    return {
        shortName: shortName,
        fullName: fullName,
        brigadiers: selectedBrigadiersArray,
        workers: selectedWorkersArray,
        zone: zone
    };
}

//______________________________________________________________
//Логика загрузки данных из excel

//Сначала нужна новая таблица под загрузку данных
function createExcelAddressTable() {
    return new Tabulator("#excel-address-table", {
        maxHeight: "80%",
        layout: "fitData",
        pagination: "local",
        paginationSize: 10,
        paginationSizeSelector: [10, 20, 30, 50, 100, true],
        paginationCounter: "rows",
        columns: [
            {title: "Id", field: "id", sorter: "number"},
            {title: "Название", field: "shortName", editor: true},
            {title: "Полный адрес", field: "fullName", editor: true},
            {title: "Зона", field: "zone",editor: true},
        ]
    });
}

//Кнопка вызывает отдельную таблицу для загрузки новых рядов из excel
document.querySelector("#import-local-file").addEventListener("click", function () {
    showExcelBox();
})

//Слушатель нажатия на импорт файла
document.querySelector("#choose-local-file").addEventListener("click",function () {
    excelAddressTable.import("xlsx", ".xlsx", "buffer")
        .then(() => {
            excelAddressTable.alert("Успешно. Не забудьте нажать на кнопку \"Сохранить изменения\"");
            setTimeout(function () {
                excelAddressTable.clearAlert();
            }, 4000)
        })
        .catch(() => {
            excelAddressTable.alert("Ошибка. Не удалось загрузить", "error");
            setTimeout(function () {
                excelAddressTable.clearAlert();
            }, 3000)
            throw new Error("Local file import error");
        })
})

//Слушатель начала работы с excel импортом
function showExcelBox(){
    let tableEl = document.querySelector("#excel-address-table");
    let tableDiv = document.querySelector("#excel-address-box")
    tableDiv.style.opacity = "1";
    tableDiv.style.visibility = "visible";
    tableEl.style.opacity = "1";
    tableEl.style.visibility = "visible";
    let main = document.querySelector("main");
    main.style.opacity = "0.5";
    main.style.filter = "blur(5px)";
    document.body.style.overflow = "visible"; //Убрали возможность скролить после нажатия
    document.getElementById("overlay").style.display = "block";
}

document.querySelector("#excel-address-exit").addEventListener("click",function () {
    closeExcelAddress();
})
function closeExcelAddress(){
    let excelTable = document.querySelector("#excel-address-table");

    excelTable.style.visibility = "hidden";
    excelTable.style.opacity = "0";

    let tableDiv = document.querySelector("#excel-address-box");
    tableDiv.style.opacity = "0";
    tableDiv.style.visibility = "hidden";
    let main = document.querySelector("main");
    main.style.opacity = "1";
    main.style.filter = "blur(0px)";
    document.body.style.overflow = "visible"; //Убрали возможность скролить после нажатия
    document.getElementById("overlay").style.display = "none";
}

//Слушатель кнопки сохранения новых адресов из excel
document.querySelector("#save-excel-address-update").addEventListener("click",function () {
sendExcelAddressesOnServer();
})

//Отправляем на сервер новые адреса из excel
function sendExcelAddressesOnServer(){
    fetch("/tables/address/save_fresh_address_list",{
        method: "POST",
        headers: {
            "Content-Type":"Application/json"
        },
        body: JSON.stringify(excelAddressTable.getData())
    }).then(response => {
        if (!response.ok){
            excelAddressTable.alert("Не удалось сохранить","error")
            setTimeout(function () {
                excelAddressTable.clearAlert();
            }, 3000)
            throw new Error("Excel address save error");
        }
        else {
            excelAddressTable.alert("Новые адреса сохранены");
            setTimeout(function () {
                excelAddressTable.clearAlert();
            }, 3000)
            excelAddressTable.clearData();
            addressTable = createAddressTable();
        }
    })
}


