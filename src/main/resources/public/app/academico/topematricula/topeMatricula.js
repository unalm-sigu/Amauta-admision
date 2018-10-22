Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#topematriculaVUE',
    data: {
        matriculaTempo: {},
        matriculaBD: [],
        matricula: [],
        verForm: false

    },
    mounted() {
        let $vue = this;
        $(".numerico").numeric({negative: false});
    },
    methods: {
        verMatriculas() {
            let $vue = this;

            $.ajax({
                method: "POST",
                contentType: "application/json",
                url: APP.url("academico/topematricula/list")
            }).then(response => {
                if (response.success) {
                    $vue.matriculaBD = response.data;
                } else {
                    notify(response.message, 'error');
                }
            }, error => {
                notify(MESSAGES.errorComunicacion, 'error');
            });
        }
//        verGuardar() {
//            var form = $("#formMatriculados");
//            if (!form.parsley().validate()) {
//                return;
//            }
//
//            let $vue = this;
//            bootbox.confirm({
//                message: '¿Está seguro que desea guarda esta matrícula?',
//                buttons: {
//                    confirm: {label: 'Si, guardar', className: 'btn-success'},
//                    cancel: {label: 'No', className: 'btn-link'}
//                },
//                callback: function (aceptar) {
//                    if (aceptar) {
//                        setTimeout(function () {
//                            $vue.guardar();
//                        }, 200);
//                    }
//                }
//            });
//        },
//        guardar() {
//            let $vue = this;
//
//            $.ajax({
//                method: "POST",
//                contentType: "application/json",
//                url: APP.url("academico/topematricula/save"),
//                //data: JSON.stringify($vue.factordist)
//            }).then(response => {
//                if (response.success) {                    
//                    notify(response.message, "info")
//                    //$vue.verFactorDistanciaByDepartamentos($vue.departamentoTempo);
//                } else {
//                    notify(response.message, 'error');
//                }
//            }, error => {
//                notify(MESSAGES.errorComunicacion, 'error');
//            });
//        },
//        verMatricula() {
//
//            let $vue = this;
//
//            $.ajax({
//                method: "POST",
//                contentType: "application/json",
//                url: APP.url("academico/topematricula/list")
//            }).then(response => {
//                if (response.success) {
//                    $vue.matriculaBD = response.data;
//                    $vue.verForm=false;
//                    $vue.factordist = [];
//                    for (var i = 0; i < $vue.matriculas.length; i++) {
//                        let fac = $vue.getFactorModulo($vue.modulos[i], $vue.factorBD);
//                        if (fac == null) {
//                            $vue.factordist.push({pabellon: $vue.modulos[i], departamentoAcademico: item, distancia: ""});
//                        } else {
//                            $vue.factordist.push(fac);
//                        }
//
//                    }
//                } else {
//                    notify(response.message, 'error');
//                }
//            }, error => {
//                notify(MESSAGES.errorComunicacion, 'error');
//            });
//        },
//        getFactorModulo(pa, factordist) {
//            for (var i = 0; i < factordist.length; i++) {
//                if (pa.codigo == factordist[i].pabellon.codigo) {
//                    return factordist[i];
//                }
//            }
//            return null;
//        }
    }

});


