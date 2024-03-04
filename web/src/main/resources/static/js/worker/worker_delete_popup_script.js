jQuery(document).ready(function($){

    //Закрываем, если не удаляем
    $('#equip-popup-no').on('click', function(event){
        event.preventDefault();
        $('.cd-popup').removeClass('is-visible');
    });

    //Удаляем выбранные ряды, если да
    $('#worker-popup-yes').on('click', function(event) {
        event.preventDefault();
        $('.cd-popup').removeClass('is-visible');
        workerTable.alert("Удаляем записи...", "msg");
        setTimeout(function (){deleteRows()},1000)
    });

    function deleteRows(){
        let deletedIdsArray = [];
        for (selectedRow of workerTable.getSelectedRows()) {
            deletedIdsArray.push(selectedRow.getData().id)
            selectedRow.delete()
        }
        fetch("/tables/worker/delete_worker_rows", {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(deletedIdsArray)
        })
            .then(response => {
                if (!response.ok) {
                    workerTable.clearAlert();
                    workerTable.alert("Ошибка. Изменения не сохранены", "error");
                    setTimeout(function () {
                        workerTable.clearAlert();
                    }, 4000)
                    throw new Error('DB error')
                } else {
                    workerTable.setData("/tables/worker/main_table").then(function () {
                        workerTable.clearAlert();
                        workerTable.alert("Записи удалены", "msg");
                        setTimeout(function () {
                            workerTable.clearAlert();
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