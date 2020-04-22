new Vue({
    el: '#main',
    mixins: [VueLoader],
    data: {
        aula: {id: idaula},
        inventarioURL: APP.url('general/aula/inventario/' + idaula + '/allresumen'),
    },
    mounted: function () {
        let $vue = this;
    },
    updated: function () {
    },
    methods: {
        changeEstadoVisible(item) {
            console.log(item.visibleReporteParcial);
            var vue = this;
            vue.showLoader();
            var valuee= item.visibleReporteParcial?0:1;
            $.ajax({
                method: 'POST',
                url: APP.url('general/aula/inventario/updateresumen'),
                async: false,
                data: {id:item.id,visibleReporteParcial:valuee},
                success: function (response) {
                    if (response.success) {
                        notify(response.message, 'info');
                    } else {
                        notify(response.message, 'error');
                    }
                    vue.hideLoader();
                }, error: function () {
                    vue.hideLoader();
                    notify(Messages.errorComunicacion, "error");
                }
            });
        }
    }
});      