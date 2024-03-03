jQuery(document).ready(function($){

    //Закрываем, если не удаляем
    $('#equip-popup-no').on('click', function(event){
        event.preventDefault();
        $('.cd-popup').removeClass('is-visible');
    });

    //Удаляем выбранные ряды, если да
    $('#equip-popup-yes').on('click', function(event) {
        event.preventDefault();
        $('.cd-popup').removeClass('is-visible');
        equipTable.alert("Удаляем записи...", "msg");
        setTimeout(function (){deleteRows()},1000)
    });

    function deleteRows(){
        let deletedIdsArray = [];
        for (selectedRow of equipTable.getSelectedRows()) {
            deletedIdsArray.push(selectedRow.getData().id)
            selectedRow.delete()
        }
        fetch("/tables/equip/delete_equip_rows", {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(deletedIdsArray)
        })
            .then(response => {
                if (!response.ok) {
                    equipTable.clearAlert();
                    equipTable.alert("Ошибка. Изменения не сохранены", "error");
                    setTimeout(function () {
                        equipTable.clearAlert();
                    }, 4000)
                    throw new Error('DB error')
                } else {
                    equipTable.setData("/tables/equip/main_table").then(function () {
                        equipTable.clearAlert();
                        equipTable.alert("Записи удалены", "msg");
                        setTimeout(function () {
                            equipTable.clearAlert();
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