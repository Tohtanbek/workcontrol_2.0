jQuery(document).ready(function($){

    //Закрываем, если не удаляем
    $('#shift-popup-no').on('click', function(event){
        event.preventDefault();
        $('.cd-popup').removeClass('is-visible');
    });

    //Удаляем выбранные ряды, если да
    $('#shift-popup-yes').on('click', function(event) {
        event.preventDefault();
        $('.cd-popup').removeClass('is-visible');
        shiftTable.alert("Удаляем записи...", "msg");
        setTimeout(function (){deleteRows()},1000)
    });

    function deleteRows(){
        let deletedIdsArray = [];
        for (selectedRow of shiftTable.getSelectedRows()) {
            deletedIdsArray.push(selectedRow.getData().id)
            selectedRow.delete()
        }
        fetch("/tables/shift/delete_shift_rows", {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(deletedIdsArray)
        })
            .then(response => {
                if (!response.ok) {
                    shiftTable.clearAlert();
                    shiftTable.alert("Ошибка. Изменения не сохранены", "error");
                    setTimeout(function () {
                        shiftTable.clearAlert();
                    }, 4000)
                    throw new Error('DB error')
                } else {
                    shiftTable.setData("/tables/shift/main_table").then(function () {
                        shiftTable.clearAlert();
                        shiftTable.alert("Записи удалены", "msg");
                        setTimeout(function () {
                            shiftTable.clearAlert();
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