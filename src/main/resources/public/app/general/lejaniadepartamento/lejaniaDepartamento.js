Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#lejaniadepartamentoVUE',
    data: {
        departamentosURL: APP.url('general/lejaniadepartamento/list'),
        confirmarModal: {
            id: 'modalConfirmar',
            header: true,
            title: 'Configurar Lejanía Facultad Módulo',
            cancelbtn: 'Cancelar',
            okbtn: 'Guardar',
            modalsize: 'modal-md'
        },
        departamentoTempo: {},
        departamentos: [],
        factordist: [],
        factorBD: [],
        factordistancia: [],

    },
    mounted() {
        let $vue = this;
        $(".numerico").numeric({negative: false});
        $vue.loadModulos();
    },
    methods: {
        verGuardar() {
            var form = $("#formDepartamentosModulos");
            if (!form.parsley().validate()) {
                return;
            }

            let $vue = this;
            bootbox.confirm({
                message: '¿Está seguro que desea guarda esta lejanía?',
                buttons: {
                    confirm: {label: 'Si, guardar', className: 'btn-success'},
                    cancel: {label: 'No', className: 'btn-link'}
                },
                callback: function (aceptar) {
                    if (aceptar) {
                        setTimeout(function () {
                            $vue.guardar();
                        }, 200);
                    }
                }
            });
        },
        guardar() {
            let $vue = this;

            $.ajax({
                method: "POST",
                contentType: "application/json",
                url: APP.url("general/lejaniadepartamento/save"),
                data: JSON.stringify($vue.factordist)
            }).then(response => {
                if (response.success) {
                    $vue.$refs.modalConfirmar.close();
                    $vue.$refs.raptorDepartamentos.loadRemoteData();
                    notify(response.message, "info")
                } else {
                    notify(response.message, 'error');
                }
            }, error => {
                notify(MESSAGES.errorComunicacion, 'error');
            });
        },
        verNuevoLejania() {
            let $vue = this;

            $vue.confirmarModal.title = "Configurar Lejanía Facultad Módulo";
            $vue.departamentoTempo = {id: '', facultad: {}};
            $.ajax({
                method: "POST",
                contentType: "application/json",
                url: APP.url("general/lejaniadepartamento/allDepartamentos")
            }).then(response => {
                if (response.success) {
                    $vue.departamentos = response.data;
                    $vue.$refs.modalConfirmar.open();
                } else {
                    notify(response.message, 'error');
                }
            }, error => {
                notify(MESSAGES.errorComunicacion, 'error');
            });
        },
        verFactorDistanciaByDepartamentos(item) {
            console.log(item.id);

            let $vue = this;

            $.ajax({
                method: "POST",
                contentType: "application/json",
                url: APP.url("general/lejaniadepartamento/" + item.id + "/allFactorDistanciaByDepartamento")
            }).then(response => {
                if (response.success) {
                    $vue.factorBD = response.data;
                    $vue.factordist = [];
                    for (var i = 0; i < $vue.modulos.length; i++) {
                        let fac = $vue.getFactorModulo($vue.modulos[i], $vue.factorBD);
                        if (fac == null) {
                            $vue.factordist.push({pabellon: $vue.modulos[i], departamentoAcademico: item, distncia: ""});
                        } else {
                            $vue.factordist.push(fac);
                        }

                    }
                } else {
                    notify(response.message, 'error');
                }
            }, error => {
                notify(MESSAGES.errorComunicacion, 'error');
            });
        },
        getFactorModulo(pa, factordist) {
            for (var i = 0; i < factordist.length; i++) {
                if (pa.codigo == factordist[i].pabellon.codigo) {
                    return factordist[i];
                }
            }
            return null;
        },
        loadModulos() {
            let $vue = this;

            $.ajax({
                method: "POST",
                contentType: "application/json",
                url: APP.url("general/lejaniadepartamento/allModulos")
            }).then(response => {
                if (response.success) {
                    $vue.modulos = response.data;
                } else {
                    notify(response.message, 'error');
                }
            }, error => {
                notify(MESSAGES.errorComunicacion, 'error');
            });
        }
    }

});


