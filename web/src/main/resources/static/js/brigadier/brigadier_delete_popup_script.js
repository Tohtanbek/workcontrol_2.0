jQuery(document).ready(function($){

    //Закрываем, если не удаляем
    $('#equip-popup-no').on('click', function(event){
        event.preventDefault();
        $('.cd-popup').removeClass('is-visible');
    });

    //Удаляем выбранные ряды, если да
    $('#brigadier-popup-yes').on('click', function(event) {
        event.preventDefault();
        $('.cd-popup').removeClass('is-visible');
        brigadierTable.alert("Удаляем записи...", "msg");
        setTimeout(function (){deleteRows()},1000)
    });

    function deleteRows(){
        let deletedIdsArray = [];
        for (selectedRow of brigadierTable.getSelectedRows()) {
            deletedIdsArray.push(selectedRow.getData().id)
            selectedRow.delete()
        }
        fetch("/tables/brigadier/delete_brigadier_rows", {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(deletedIdsArray)
        })
            .then(response => {
                if (!response.ok) {
                    brigadierTable.clearAlert();
                    brigadierTable.alert("Ошибка. Изменения не сохранены", "error");
                    setTimeout(function () {
                        brigadierTable.clearAlert();
                    }, 4000)
                    throw new Error('DB error')
                } else {
                    brigadierTable.setData("/tables/brigadier/main_table").then(function () {
                        brigadierTable.clearAlert();
                        brigadierTable.alert("Записи удалены", "msg");
                        setTimeout(function () {
                            brigadierTable.clearAlert();
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