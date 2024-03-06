let updatedRows = [];

let expenseMenu = createExpenseMenu();
let expenseTable = createExpenseTable()



//Таблица смен-------------------------------------------------


//Сама таблица смен
function createExpenseTable() {
    return new Tabulator("#expense-table", {
        ajaxURL: "/tables/expense/main_table",
        maxHeight: "80%",
        selectableRows: true,
        movableColumns: true,
        addRowPos: "top",
        pagination: "local",
        paginationSize: 10,
        paginationSizeSelector: [10, 20, 30, 50, 100, true],
        paginationCounter: "rows",
        layout: "fitDataTable",
        initialSort:[
            {column:"id", dir:"desc"}, //sort by this first
        ],
        rowContextMenu: expenseMenu,
        columns: [
            {title: "Id", field: "id", sorter: "number"},
            {title: "Отчет", field: "shortInfo", editor: true},
            {title: "Сумма", field: "totalSum"},
            {title: "Статус", field: "status", editor: "list", editorParams: {
                    autocomplete: true, valuesLookup:"active",valuesLookupField:"status",freetext:true
                }},
            {title: "Тип", field: "type", editor: "list", editorParams: {
                    autocomplete: true, valuesLookup:"active",valuesLookupField:"type",freetext:true
                }},
            {title: "Зона", field: "zone"},
            {title: "Объект", field: "address"},
            {title: "Работник", field: "worker"},
            {title: "Рабочий день",field: "shift"}
        ]
    });
}

function createExpenseMenu(){
    return [
        {
            label: "<i class='fas fa-user'></i>Удалить выбранные ряды",
            action: function (e, row) {
                $('#expense-delete-popup').addClass('is-visible');
            }
        }
    ];
}

//Фильтры для основной таблицы
let filterColumn = document.getElementById("filter-column");
let filterType = document.getElementById("filter-type");
let filterInput = document.getElementById("filter-input");
let filterClearButton = document.getElementById("clear-filter");
filterColumn.addEventListener("change",updateFilter);
filterType.addEventListener("change",updateFilter);
filterInput.addEventListener("keyup",updateFilter);

function updateFilter(){
    let filterColumnValue = filterColumn.options[filterColumn.selectedIndex].value
    let filterTypeValue = filterType.options[filterType.selectedIndex].value;
    let filterInputValue = filterInput.value;
    //Если выбрана для фильтра колонка startDate или endDate, то вставляем кастомный фильтр для дат
    if (filterColumnValue === "startDate" || filterColumnValue === "endDate"){
        let columnDateTime = new Date(filterColumnValue);
        let inputDateTime = new Date(filterInputValue);
        if (filterTypeValue === "<"){
            expenseTable.setFilter(dateTimeFilterLess(columnDateTime,inputDateTime))
        }
        else if (filterTypeValue === ">"){
            expenseTable.setFilter(dateTimeFilterBigger(columnDateTime,inputDateTime))
        }
    }else {
        expenseTable.setFilter(filterColumnValue,filterType.value,filterInput.value);
    }

}
//Очистка фильтров слушатель
filterClearButton.addEventListener("click",function (){
    filterColumn.value = "";
    filterInput.value = "";
    expenseTable.clearFilter();
})

//Кастомный фильтр для дат
function dateTimeFilterLess(columnDateTime, inputDateTime){
    return inputDateTime<columnDateTime;
}
//Кастомный фильтр для дат
function dateTimeFilterBigger(columnDateTime, inputDateTime){
    return inputDateTime>columnDateTime;
}

//-------------------------------------------------


//При нажатии кнопки коллекция с измененными dto отправляется на сервер и очищается
document.querySelector("#main-table-save-update")
    .addEventListener("click",function () {
    fetch("/tables/expense/update_expense_rows",{
        method: "PUT",
        headers:{
            "Content-Type":"application/json"
        },
        body: JSON.stringify(updatedRows)
    }).then(response => {
        if (!response.ok){
            updatedRows = [];
            expenseTable.alert("Ошибка. Изменения не сохранены","error");
            setTimeout(function (){
                expenseTable.clearAlert();
            },3000)
            throw new Error('DB error')
        }
        else {
            updatedRows = [];
            expenseTable.alert("Изменения сохранены успешно");
            setTimeout(function (){
                expenseTable.clearAlert();
            },2000)
        }
    })
})

//Слушатель обновления рядов в основной таблице. Сохраняет ряды, в которых внесены изменения
expenseTable.on("cellEdited",function (cell){
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

//Переход к нопке добавить ряд-----------------------------------------------------
//кнопка "добавить ряд" (выводит форму для нового ряда)
document.getElementById("add-row-button").addEventListener("click",async function (){
    let expenseForm = document.querySelector(".expense-form");
    expenseForm.style.display = "block";
    setTimeout(() => {
        expenseForm.style.opacity = "1";
    }, 50);
    let main = document.querySelector("main");
    main.style.opacity = "0.5";
    main.style.filter = "blur(5px)";
    document.body.style.overflow = "hidden"; //Убрали возможность скролить после нажатия
    document.getElementById("overlay").style.display = "block";

    //Загружаем типы в форму из таблицы typeTable (но сначала очищаем, чтобы не дублировались):
    let addressSelect = document.querySelector("#address");
    while (addressSelect.firstChild) {
        addressSelect.removeChild(addressSelect.firstChild);
    }
    //Добавляем варианты адресов
    let freshOption = document.createElement("option");
    freshOption.selected = true;
    freshOption.hidden = true;
    freshOption.value = "default";
    freshOption.text = "Выберите объект";
    addressSelect.appendChild(freshOption);
    let addressIdMap;
    loadAddressJsonMap().then(jsonMap => {
        addressIdMap = jsonMap;
        console.log(addressIdMap);
        for (let entry in addressIdMap){
            let freshOption = document.createElement("option");
            freshOption.value = addressIdMap[entry]
            freshOption.text = addressIdMap[entry]
            addressSelect.appendChild(freshOption);
        }
    })

})
//Отправляем форму и ожидаем ответа, чтобы обновить таблицу, не обновляя всю страницу
document.getElementById("main-form-submit").addEventListener("click",
    async function (event){
        event.preventDefault();
        let form = document.getElementById("main-form");
        const formData = new FormData(form);
        try {
            let response = await fetch('/tables/expense/add_expense_row', {
                method: "POST",
                headers: {
                    "Content-Type":"application/json"
                },
                body: JSON.stringify(Object.fromEntries(formData))
            });
            console.log(JSON.stringify(Object.fromEntries(formData)))
            if (!response.ok) {
                throw new Error('Ошибка при отправке формы');
            }
            else {
                console.log("Форма на создание оборудования создана успешно");
                expenseTable.setData("/tables/expense/main_table");
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

// Метод загрузки адресов для формы
async function loadAddressJsonMap() {
    try {
        let response = await fetch("/tables/address/load_address_map", {
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

//-----------------------------------------------------------------------------------

