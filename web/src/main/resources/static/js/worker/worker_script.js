let workerTable = createWorkerTable();

//кнопка "добавить ряд" (выводит форму для нового ряда)
document.getElementById("add-row-button").addEventListener("click",function (){
    let workerForm = document.querySelector(".worker-form");
    workerForm.style.display = "block";
    setTimeout(()=>workerForm.style.opacity = "1",50);
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
    let form = document.getElementById("main-form");
    const formData = new FormData(form);
    try {
        let response = await fetch('/tables/worker/add_worker_row', {
            method: "POST",
            headers: {
                "Content-Type":"application/json"
            },
            body: JSON.stringify(Object.fromEntries(formData))
        });
        if (!response.ok) {
            throw new Error('Ошибка при отправке формы');
        }
        else {
            console.log("Форма на создание работника создана успешно");
            workerTable.setData("/tables/worker/main_table");
            $('#form-popup').addClass('is-visible');
        }
    } catch (error){
        console.error('Ошибка при отправке формы', error);
    }
})
//Крестик (закрыть форму)
document.getElementById("form-exit").addEventListener("click",function (){
    let workerForm = document.querySelector(".worker-form");
    workerForm.style.opacity = "0";
    setTimeout(function (){workerForm.style.display = "none"},300);//Чтобы была анимация
    let main = document.querySelector("main");
    main.style.opacity = "1";
    main.style.filter = "blur(0px)";
    document.body.style.overflow = "visible"; //Вернули возможность скролить после нажатия
    document.getElementById("overlay").style.display = "none";
    let typeSelect = document.querySelector("#type-select");
})
//-----------------------------------------------------------------------------------

//Добавляем таблицы------------------------------------
function createWorkerTable(){
    return new Tabulator("#worker-table",{
        ajaxURL: "/tables/worker/main_table",
        maxHeight: "80%",
        selectableRows: true,
        movableColumns: true,
        addRowPos: "top",
        pagination: "local",
        paginationSize: 10,
        paginationSizeSelector: [10, 20, true],
        paginationCounter: "rows",
        layout: "fitDataStretch",
        rowContextMenu: createWorkerMenu(),
        columns: [
            {title:"Id", field: "id"},
            {title: "Имя", field: "name"},
            {title: "Телефон",field: "phoneNumber"},
            {title: "Специальность",field: "job"}
        ]
    })
}

function createWorkerMenu(){
    return [
        {
            label: "<i class='fas fa-user'></i>Удалить выбранные ряды",
            action: function (e, row) {
                $('#worker-delete-popup').addClass('is-visible');
            }
        }
    ];
}


//_________________________________________________________________-


function closeForm(){
    let workerForm = document.querySelector(".worker-form");
    workerForm.style.opacity = "0";
    setTimeout(function (){workerForm.style.display = "none"},300);//Чтобы была анимация
    let main = document.querySelector("main");
    main.style.opacity = "1";
    main.style.filter = "blur(0px)";
    document.body.style.overflow = "visible"; //Вернули возможность скролить после нажатия
    document.getElementById("overlay").style.display = "none";
}

