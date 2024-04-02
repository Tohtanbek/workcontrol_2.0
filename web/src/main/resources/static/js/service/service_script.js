let updatedRows = [];

//Валидатор, запрещающий менять статус после того, как смена закрыта
let discountLessThan101 = function(cell, value){
    return value<101
}

let serviceMenu = createServiceMenu();
let serviceTable = createServiceTable();
//Переход к нопке добавить ряд-----------------------------------------------------
//кнопка "добавить ряд" (выводит форму для нового ряда)
document.getElementById("add-row-button").addEventListener("click",async function (){
    let serviceForm = document.querySelector(".service-form");
    serviceForm.style.display = "block";
    setTimeout(() => {
        serviceForm.style.opacity = "1";
    }, 50);
    let main = document.querySelector("main");
    main.style.opacity = "0.5";
    main.style.filter = "blur(5px)";
    document.body.style.overflow = "hidden"; //Убрали возможность скролить после нажатия
    document.getElementById("overlay").style.display = "block";
})
//Отправляем форму и ожидаем ответа, чтобы обновить таблицу, не обновляя всю страницу
document.getElementById("main-form-submit").addEventListener("click",
    async function (event){
        event.preventDefault();
        let formJson = JSON.stringify(createFormJson());
        console.log(formJson)
        try {
            let response = await fetch('/tables/service/add_service_row', {
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
                console.log("Форма на создание оборудования создана успешно");
                serviceTable.setData("/tables/service/main_table");
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
function createServiceTable() {
    return new Tabulator("#service-table", {
        ajaxURL: "/tables/service/main_table",
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
        rowContextMenu: serviceMenu,
        columns: [
            {title: "Id", field: "id", sorter: "number"},
            {title: "Название", field: "name", editor: true},
            {title: "Категория", field: "category", editor: "list",
                editorParams: {
                    values:["MAIN", "EXTRA"]
                },
            },
            {title: "Цена", field: "price",editor: "number"},
            {title: "Минимальная цена", field: "minimalPrice",editor: "number"},
            {title: "Промокод", field: "promoCode",editor: true},
            {
                title: "Скидка промокода", field: "promoCodeDiscount", editor: "number",
                editorParams:
                    {
                        min: 0,
                        max: 100
                    },
                validator:[
                    {
                        type:(discountLessThan101),
                    }
                ]
            }
        ]
    });
}

function createServiceMenu(){
    return [
        {
            label: "<i class='fas fa-user'></i>Удалить выбранные ряды",
            action: function (e, row) {
                $('#service-delete-popup').addClass('is-visible');
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
    serviceTable.setFilter(filterColumnValue,"like",filterInput.value);

    filterClearButton.addEventListener("click",function (){
        filterColumn.value = "";
        filterInput.value = "";
        serviceTable.clearFilter();
    })
}

function closeForm(){
    let serviceForm = document.querySelector(".service-form");
    serviceForm.style.opacity = "0";
    setTimeout(function (){serviceForm.style.display = "none"},300);//Чтобы была анимация
    let main = document.querySelector("main");
    main.style.opacity = "1";
    main.style.filter = "blur(0px)";
    document.body.style.overflow = "visible"; //Вернули возможность скролить после нажатия
    document.getElementById("overlay").style.display = "none";
}


//При нажатии кнопки коллекция с измененными dto отправляется на сервер и очищается
document.querySelector("#main-table-save-update")
    .addEventListener("click",function () {
    fetch("/tables/service/update_service_rows",{
        method: "PUT",
        headers:{
            "Content-Type":"application/json"
        },
        body: JSON.stringify(updatedRows)
    }).then(response => {
        if (!response.ok){
            updatedRows = [];
            serviceTable.alert("Ошибка. Изменения не сохранены","error");
            setTimeout(function (){
                serviceTable.clearAlert();
            },3000)
            throw new Error('DB error')
        }
        else {
            updatedRows = [];
            serviceTable.alert("Изменения сохранены успешно");
            setTimeout(function (){
                serviceTable.clearAlert();
            },2000)
        }
    })
})

//Слушатель обновления рядов в основной таблице. Сохраняет ряды, в которых внесены изменения
serviceTable.on("cellEdited",function (cell){
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

    let name = document.querySelector("#name");
    let category = document.querySelector("#category");
    let price = document.querySelector("#price");
    let minPrice = document.querySelector("#minimalPrice");
    let promoCode = document.querySelector("#promoCode");
    let promoCodeDiscount = document.querySelector("#promoCodeDiscount");

    if (!checkForm([name,category,price])){
        throw new Error("Not valid input")
    }

    return {
        name: name.value,
        category: category.value,
        price: price.value,
        minimalPrice: minPrice.value,
        promoCode: promoCode.value,
        promoCodeDiscount: promoCodeDiscount.value
    };
}

function checkForm(attributes){
    let result = true;
    for (let attribute of attributes){
        if (!attribute.value || !attribute.validity.valid) {
            attribute.reportValidity()
            result = false;
        }else {
            attribute.setCustomValidity('');
        }
    }
    return result;
}
