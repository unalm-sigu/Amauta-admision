
new Vue({
    el: '#tipoConstanciaVue',
    data: {
        tipoConstancia: JSON.parse(tipoDocumentoJson),
        idiomas: JSON.parse(idiomasJson),
        isNew: true,
        isOld: false,
        costoDocumento: {}
    },
    computed: {

    },
    created() {
        let $vue = this;
    },
    mounted: function () {
    },
    methods: {
        modalUpdate: function (id, lista) {
            let $vue = this;
            $vue.costoDocumento = {};
            lista.forEach(function (elem) {
                if (id == elem.id) {
                    $vue.costoDocumento = elem;
                }
            })
            $("#myModal").modal('show');
            $vue.isNew = false;
        },
        nuevo: function () {
            let $vue = this;
            $vue.isNew = true;
            $vue.costoDocumento = {};
            $("#myModal").modal('show');
        },
        update: function (e) {
            var self = $(e.currentTarget);
            self.btnDisabled();
            $(".mx-input").attr("required", true);
            if (!$("#formConfig").parsley().validate() == true) {
                self.btnEnable();
                return;
            }
            self.btnEnable();
            let $vue = this;
            $vue.costoDocumento.tipoDocumento.tipo = $vue.costoDocumento.tipoDocumento.tipo.name;
            $vue.costoDocumento.tipoDocumento.costoCiclo = $vue.costoDocumento.tipoDocumento.costoCiclo == true ? 1 : 0;
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/costodocumento/update'),
                contentType: "application/json",
                data: JSON.stringify($vue.costoDocumento),
                success: function (response) {
                    if (response.success) {
                        $global.$emit("reloadDyntable");
                        notify(response.message, 'info');
                    }
                }
            });
            $("#myModal").modal('hide');
        },
        save: function (e) {
            var self = $(e.currentTarget);
            self.btnDisabled();
            $(".mx-input").attr("required", true);

            if (!$("#formConfig").parsley().validate()) {
                self.btnEnable();
                return;
            }
            self.btnEnable();
            let $vue = this;
            console.log($vue.costoDocumento);
            $vue.costoDocumento.tipoDocumento.tipo = $vue.costoDocumento.tipoDocumento.tipo.name;
            $vue.costoDocumento.tipoDocumento.costoCiclo = $vue.costoDocumento.tipoDocumento.costoCiclo == true ? 1 : 0;
            $.ajax({
                method: 'POST',
                url: APP.url('tramite/costodocumento/save'),
                contentType: "application/json",
                data: JSON.stringify($vue.costoDocumento),
                success: function (response) {
                    if (response.success) {
                        $global.$emit("reloadDyntable");
                        notify(response.message, 'info');

                    } else {
                        notify(response.message, 'error');
                    }
                }
            });
            $("#myModal").modal('hide');
        },
    }
});
