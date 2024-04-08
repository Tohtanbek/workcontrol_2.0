jQuery(document).ready(function($){

    //Закрываем, если не удаляем
    $('#assign-equip-popup-no').on('click', function(event){
        event.preventDefault();
        $('.cd-popup').removeClass('is-visible');
    });

    //Удаляем выбранные ряды, если да
    $('#assign-equip-popup-yes').on('click', function(event) {
        event.preventDefault();
        $('.cd-popup').removeClass('is-visible');
        assignEquipTable.alert("Удаляем записи...", "msg");
        setTimeout(function (){deleteRows()},1000)
    });

    function deleteRows(){
        let deletedIdsArray = [];
        for (selectedRow of assignEquipTable.getSelectedRows()) {
            deletedIdsArray.push(selectedRow.getData().id)
            selectedRow.delete()
        }
        fetch("/tables/assignment_equip/delete_assignment_equip_rows", {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(deletedIdsArray)
        })
            .then(response => {
                if (!response.ok) {
                    assignEquipTable.clearAlert();
                    assignEquipTable.alert("Ошибка. Изменения не сохранены", "error");
                    setTimeout(function () {
                        assignEquipTable.clearAlert();
                    }, 4000)
                    throw new Error('DB error')
                } else {
                    assignEquipTable.setData("/tables/assignment_equip/main_table").then(function () {
                        assignEquipTable.clearAlert();
                        assignEquipTable.alert("Записи удалены", "msg");
                        setTimeout(function () {
                            assignEquipTable.clearAlert();
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