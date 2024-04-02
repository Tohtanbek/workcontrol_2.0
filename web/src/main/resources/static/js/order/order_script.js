let updatedRows = [];

let orderMenu = createOrderMenu();
let orderTable = createOrderTable()



//Таблица заказов-------------------------------------------------

//Сама таблица заказов
function createOrderTable() {
    return new Tabulator("#order-table", {
        ajaxURL: "/tables/order/main_table",
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
        rowContextMenu: orderMenu,
        columns: [
            {title: "Id", field: "id", sorter: "number"},
            {title: "Отчет", field: "shortInfo", editor: true},
            {title: "Статус", field: "status", editor: "list", editorParams: {
                    valuesLookup: "active", autocomplete: true, freetext: true
                }},
            {title: "Сумма", field: "total"},
            {title: "Позиции", field: "serviceList"},
            {title: "Телефон", field: "phoneNumber", editor:"number"},
            {title: "Email", field: "email", editor: true},
            {title: "Адрес", field: "address", editor: true},
            {title: "Площадь", field: "area"},
            {title: "Дата", field: "dateTime",sorter:"date"},
        ]
    });
}

function createOrderMenu(){
    return [
        {
            label: "<i class='fas fa-user'></i>Удалить выбранные ряды",
            action: function (e, row) {
                $('#order-delete-popup').addClass('is-visible');
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
    //Если выбрана для фильтра колонка startDate или endDate, то вставляем кастомный фильтр для дат
    if (filterColumnValue === "dateTime"){
        if (filterTypeValue === "<"){
            orderTable.setFilter(dateTimeFilterLess);
        }
        else if (filterTypeValue === ">"){
            orderTable.setFilter(dateTimeFilterBigger)
        }
        else {
            orderTable.setFilter(filterColumnValue,filterType.value,filterInput.value);
        }
    }else {
        orderTable.setFilter(filterColumnValue,filterType.value,filterInput.value);
    }

}

//Кастомный фильтр для дат
function dateTimeFilterLess(data){
    let filterInput = document.getElementById("filter-input");
    let filterInputValue = filterInput.value;
    let tableStartDateTime = new Date(data.supplyDate);
    let inputStartDateTime = new Date(filterInputValue);
    return tableStartDateTime.getTime()<inputStartDateTime.getTime();
}
//Кастомный фильтр для дат
function dateTimeFilterBigger(data){
    let filterInput = document.getElementById("filter-input");
    let filterInputValue = filterInput.value;
    let tableStartDateTime = new Date(data.supplyDate);
    let inputStartDateTime = new Date(filterInputValue);
    return tableStartDateTime.getTime()>inputStartDateTime.getTime();
}

//Очистка фильтров слушатель
filterClearButton.addEventListener("click",function (){
    filterColumn.value = "";
    filterInput.value = "";
    orderTable.clearFilter();
})

//-------------------------------------------------


//При нажатии кнопки коллекция с измененными dto отправляется на сервер и очищается
document.querySelector("#main-table-save-update")
    .addEventListener("click",function () {
    fetch("/tables/order/update_order_rows",{
        method: "PUT",
        headers:{
            "Content-Type":"application/json"
        },
        body: JSON.stringify(updatedRows)
    }).then(response => {
        if (!response.ok){
            updatedRows = [];
            orderTable.alert("Ошибка. Изменения не сохранены","error");
            setTimeout(function (){
                orderTable.clearAlert();
            },3000)
            throw new Error('DB error')
        }
        else {
            updatedRows = [];
            orderTable.alert("Изменения сохранены успешно");
            setTimeout(function (){
                orderTable.clearAlert();
            },2000)
        }
    })
})

//Слушатель обновления рядов в основной таблице. Сохраняет ряды, в которых внесены изменения
orderTable.on("cellEdited",function (cell){
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