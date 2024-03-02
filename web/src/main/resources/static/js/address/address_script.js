let updatedRows = [];

let addressMenu = createAddressMenu();
let equipTable = createAddressTable()
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

    //Загружаем типы в форму из таблицы typeTable (но сначала очищаем, чтобы не дублировались):
    let typeSelect = document.querySelector("#type");
    while (typeSelect.firstChild) {
        typeSelect.removeChild(typeSelect.firstChild);
    }
    //Добавляем варианты типов оборудования
    let freshOption = document.createElement("option");
    freshOption.selected = true;
    freshOption.hidden = true;
    freshOption.value = "default";
    freshOption.text = "Выберите тип";
    typeSelect.appendChild(freshOption);
    let typesArray = typeTable.getData();
    console.log(typesArray);
    for (let row of typesArray){
        let freshOption = document.createElement("option");
        freshOption.value = row['name'];
        freshOption.text = row['name'];
        typeSelect.appendChild(freshOption);
    }

    //Добавляем варианты ответственных из другой таблицы
    //Берем их по ссылке в виде массива
    try {
        let response = await fetch("/tables/supervisors/responsible_names_array", {
            method: "GET",
        });
        if (!response.ok) {
            throw new Error("Ошибка при загрузке ответственных при создании формы оборудования")
        } else {
            console.log("Ответственные загружены для выбора в форме оборудования")
            let responsibleSelect = document.querySelector("#responsible");
            while (responsibleSelect.firstChild) {
                responsibleSelect.removeChild(responsibleSelect.firstChild);
            }
            let freshOption = document.createElement("option");
            freshOption.selected = true;
            freshOption.hidden = true;
            freshOption.value = "default";
            freshOption.text = "Выберите ответственного";
            responsibleSelect.appendChild(freshOption);

            let responsibleArray = await response.json();
            for (let row of responsibleArray) {
                let freshOption = document.createElement("option");
                freshOption.value = row;
                freshOption.text = row;
                responsibleSelect.appendChild(freshOption)
            }
        }
    }
    catch (error){
        console.error('Ошибка при загрузке ответственного в форму', error);
    }
})
//Отправляем форму и ожидаем ответа, чтобы обновить таблицу, не обновляя всю страницу
document.getElementById("main-form-submit").addEventListener("click",
    async function (event){
        event.preventDefault();
        let form = document.getElementById("main-form");
        const formData = new FormData(form);
        try {
            let response = await fetch('/tables/equip/add_equip_row', {
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
                equipTable.setData("/tables/equip/main_table");
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
                $('#equip-delete-popup').addClass('is-visible');
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
    equipTable.setFilter(filterColumnValue,"like",filterInput.value);

    filterClearButton.addEventListener("click",function (){
        filterColumn.value = "";
        filterInput.value = "";
        equipTable.clearFilter();
    })
}

function closeForm(){
    let equipForm = document.querySelector(".equip-form");
    equipForm.style.opacity = "0";
    setTimeout(function (){equipForm.style.display = "none"},300);//Чтобы была анимация
    let main = document.querySelector("main");
    main.style.opacity = "1";
    main.style.filter = "blur(0px)";
    document.body.style.overflow = "visible"; //Вернули возможность скролить после нажатия
    document.getElementById("overlay").style.display = "none";
}


//При нажатии кнопки коллекция с измененными dto отправляется на сервер и очищается
document.querySelector("#main-table-save-update")
    .addEventListener("click",function () {
    fetch("/tables/equip/update_equip_rows",{
        method: "PUT",
        headers:{
            "Content-Type":"application/json"
        },
        body: JSON.stringify(updatedRows)
    }).then(response => {
        if (!response.ok){
            updatedRows = [];
            equipTable.alert("Ошибка. Изменения не сохранены","error");
            setTimeout(function (){
                equipTable.clearAlert();
            },3000)
            throw new Error('DB error')
        }
        else {
            updatedRows = [];
            equipTable.alert("Изменения сохранены успешно");
            setTimeout(function (){
                equipTable.clearAlert();
            },2000)
        }
    })
})

//Слушатель обновления рядов в основной таблице. Сохраняет ряды, в которых внесены изменения
equipTable.on("cellEdited",function (cell){
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