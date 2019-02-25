Vue.component("multiselect", window.VueMultiselect.default);
var app = new Vue({
    el: '#main',
    data: {
        carpetaUrl: APP.url("general/tipocarpeta/allTipoCarpeta"),
        tipocarpetas: []
    },
    mounted: function () {
        let $vue = this;
        $vue.loadTipoCarpeta()
    },
    methods: {
        loadTipoCarpeta() {
            let $vue = this;
            $.ajax({
                url: APP.url("general/tipocarpeta/allTipoCarpeta"),
                type: 'POST',
                async: false,
                success: function (response) {
                    if (response.success) {
                        $vue.tipocarpetas = response.data;
                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });
        },
    }
});


