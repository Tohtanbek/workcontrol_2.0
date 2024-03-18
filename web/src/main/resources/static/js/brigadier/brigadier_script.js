let brigadierTable = createBrigadierTable();
let updatedRows = [];

//кнопка "добавить ряд" (выводит форму для нового ряда)
document.getElementById("add-row-button").addEventListener("click",function (){
    let brigadierForm = document.querySelector(".brigadier-form");
    brigadierForm.style.display = "block";
    setTimeout(()=>brigadierForm.style.opacity = "1",50);
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
    try {
        let response = await fetch('/tables/brigadier/add_brigadier_row', {
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
            console.log("Форма на создание работника создана успешно");
            brigadierTable.setData("/tables/brigadier/main_table");
            $('#form-popup').addClass('is-visible');
        }
    } catch (error){
        console.error('Ошибка при отправке формы', error);
    }
})
//Крестик (закрыть форму)
document.getElementById("form-exit").addEventListener("click",function (){
    let brigadierForm = document.querySelector(".brigadier-form");
    brigadierForm.style.opacity = "0";
    setTimeout(function (){brigadierForm.style.display = "none"},300);//Чтобы была анимация
    let main = document.querySelector("main");
    main.style.opacity = "1";
    main.style.filter = "blur(0px)";
    document.body.style.overflow = "visible"; //Вернули возможность скролить после нажатия
    document.getElementById("overlay").style.display = "none";
    let typeSelect = document.querySelector("#type-select");
})
//-----------------------------------------------------------------------------------

//Добавляем таблицы------------------------------------
function createBrigadierTable(){
    return new Tabulator("#brigadier-table",{
        ajaxURL: "/tables/brigadier/main_table",
        maxHeight: "80%",
        selectableRows: true,
        movableColumns: true,
        addRowPos: "top",
        pagination: "local",
        paginationSize: 10,
        paginationSizeSelector: [10, 20, true],
        paginationCounter: "rows",
        layout: "fitDataStretch",
        rowContextMenu: createBrigadierMenu(),
        columns: [
            {title:"Id", field: "id"},
            {title: "Имя", field: "name",editor: true},
            {title: "Телефон",field: "phoneNumber", editor: "number"},
            {title: "ЗП", field: "wageRate", editor: "number"},
            {title: "Доходный коэф.", field: "incomeRate", editor: "number"},
            {title: "Почасово", field: "isHourly", formatter: "tickCross",editor: "tickCross"},
            {title: "Ответственные",field: "supervisors"}
        ]
    })
}

function createBrigadierMenu(){
    return [
        {
            label: "<i class='fas fa-user'></i>Удалить выбранные ряды",
            action: function (e, row) {
                $('#brigadier-delete-popup').addClass('is-visible');
            }
        }
    ];
}


//_________________________________________________________________-


function closeForm(){
    let brigadierForm = document.querySelector(".brigadier-form");
    brigadierForm.style.opacity = "0";
    setTimeout(function (){brigadierForm.style.display = "none"},300);//Чтобы была анимация
    let main = document.querySelector("main");
    main.style.opacity = "1";
    main.style.filter = "blur(0px)";
    document.body.style.overflow = "visible"; //Вернули возможность скролить после нажатия
    document.getElementById("overlay").style.display = "none";
}

//При нажатии кнопки коллекция с измененными dto отправляется на сервер и очищается
document.querySelector("#main-table-save-update")
    .addEventListener("click",function () {
        fetch("/tables/brigadier/update_brigadier_rows",{
            method: "PUT",
            headers:{
                "Content-Type":"application/json"
            },
            body: JSON.stringify(updatedRows)
        }).then(response => {
            if (!response.ok){
                updatedRows = [];
                brigadierTable.alert("Ошибка. Изменения не сохранены","error");
                setTimeout(function (){
                    brigadierTable.clearAlert();
                },3000)
                throw new Error('DB error')
            }
            else {
                updatedRows = [];
                brigadierTable.alert("Изменения сохранены успешно");
                setTimeout(function (){
                    brigadierTable.clearAlert();
                },2000)
            }
        })
    })

//Слушатель обновления рядов в основной таблице. Сохраняет ряды, в которых внесены изменения
brigadierTable.on("cellEdited",function (cell){
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

//Создает корректный json формы на отправку
function createFormJson(){

    let name = document.querySelector("#name").value;
    let phoneNumber = document.querySelector("#phoneNumber").value;
    let wageRate = document.querySelector('#wageRate').value;
    let incomeRate = document.querySelector('#incomeRate').value;
    let isHourly = document.querySelector('#isHourly');

    return {
        name: name,
        phoneNumber: phoneNumber,
        wageRate: wageRate,
        incomeRate: incomeRate,
        isHourly: isHourly.checked,
    };
}

