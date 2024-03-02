jQuery(document).ready(function($){

    //Закрываем, если не удаляем
    $('#equip-popup-no').on('click', function(event){
        event.preventDefault();
        $('.cd-popup').removeClass('is-visible');
    });

    //Удаляем выбранные ряды, если да
    $('#supervisor-popup-yes').on('click', function(event) {
        event.preventDefault();
        $('.cd-popup').removeClass('is-visible');
        responsibleTable.alert("Удаляем записи...", "msg");
        setTimeout(function (){deleteRows()},1000)
    });

    function deleteRows(){
        let deletedIdsArray = [];
        for (selectedRow of responsibleTable.getSelectedRows()) {
            deletedIdsArray.push(selectedRow.getData().id)
            selectedRow.delete()
        }
        fetch("/tables/supervisors/delete_supervisor_rows", {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(deletedIdsArray)
        })
            .then(response => {
                if (!response.ok) {
                    responsibleTable.clearAlert();
                    responsibleTable.alert("Ошибка. Изменения не сохранены", "error");
                    setTimeout(function () {
                        responsibleTable.clearAlert();
                    }, 4000)
                    throw new Error('DB error')
                } else {
                    responsibleTable.setData("/tables/supervisors/main_table").then(function () {
                        responsibleTable.clearAlert();
                        responsibleTable.alert("Записи удалены", "msg");
                        setTimeout(function () {
                            responsibleTable.clearAlert();
                        }, 2000)
                    })
                }
            });
    }

    //close popup when clicking the esc keyboard button
    $(document).keyup(function(event){
        if(event.which==='27'){
            $('.cd-popup').removeClass('is-visible');
        }
    });
});