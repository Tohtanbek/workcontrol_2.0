jQuery(document).ready(function($){

    //Закрываем, если не удаляем
    $('#service-popup-no').on('click', function(event){
        event.preventDefault();
        $('.cd-popup').removeClass('is-visible');
    });

    //Удаляем выбранные ряды, если да
    $('#service-popup-yes').on('click', function(event) {
        event.preventDefault();
        $('.cd-popup').removeClass('is-visible');
        serviceTable.alert("Удаляем записи...", "msg");
        setTimeout(function (){deleteRows()},1000)
    });

    function deleteRows(){
        let deletedIdsArray = [];
        for (selectedRow of serviceTable.getSelectedRows()) {
            deletedIdsArray.push(selectedRow.getData().id)
            selectedRow.delete()
        }
        fetch("/tables/service/delete_service_rows", {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(deletedIdsArray)
        })
            .then(response => {
                if (!response.ok) {
                    serviceTable.clearAlert();
                    serviceTable.alert("Ошибка. Изменения не сохранены", "error");
                    setTimeout(function () {
                        serviceTable.clearAlert();
                    }, 4000)
                    throw new Error('DB error')
                } else {
                    serviceTable.setData("/tables/service/main_table").then(function () {
                        serviceTable.clearAlert();
                        serviceTable.alert("Записи удалены", "msg");
                        setTimeout(function () {
                            serviceTable.clearAlert();
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