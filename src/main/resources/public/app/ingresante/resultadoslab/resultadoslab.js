
Vue.component("multiselect", window.VueMultiselect.default)

new Vue({
    el: '#resultadosVUE',
    data: {
        ciclo: JSON.parse(cicloJson),
        turnos: JSON.parse(turnosJson),
        turnoSelected: {},
        ingresantesURL: APP.url('ingresante/resultadoslab/list/turno/0'),
        tipoSangreList: [],
        rhList: [],
        itemSelected: {laboratorio: ''},
        newObservacionModal: VUE_MODAL.structFormAjax({
            id: 'id-modal-observacion',
            header: true,
            title: 'Observaciones',
            okbtn: 'Aceptar'
        }),
        modalVerObservacion: VUE_MODAL.structInfo({
            id: 'id-modal-ver-observacion'
        })

    },
    mounted: function () {
        let $vue = this;
        $(".decimal").numeric({negative: false});

        $vue.$refs.raptorRL.afterProcess = () => {
            $(".decimal").numeric({negative: false});
        };

        $vue.loadTipoSangre();
        $vue.loadFactorRh();
        console.log(" $vue.$refs.raptorRL", $vue.$refs.raptorRL._props);
        $vue.$refs.raptorRL.afterProcess = function () {
            $('[data-toggle="tooltip"]').tooltip();
        }

    },
    methods: {

        guardarNumeroMuestra(item) {
            let $vue = this;
            if (item.laboratorio.tipoSangreEnum != null) {
                item.laboratorio.tipoSangre = item.laboratorio.tipoSangreEnum.name;
            }
            delete item.laboratorio.tipoSangreEnum;

            if (item.laboratorio.factorRHEnum != null) {
                item.laboratorio.factorRH = item.laboratorio.factorRHEnum.name;
            }
            delete item.laboratorio.factorRHEnum;

            console.log("item selected", item)

            $.ajax({
                method: 'POST',
                url: APP.url('ingresante/resultadoslab/saveLaboratorio'),
                data: JSON.stringify(item.laboratorio),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        console.log("response", response.data);
                        notify(response.message, 'info');
                        //$vue.$refs.raptorRL.loadRemoteData();
                        item.laboratorio = response.data;
                        
                    } else {
                        notify(response.message, 'error');
                    }
                }
            });
        },
        saveOtherColumns(item) {
            delete item.laboratorio.tipoSangreEnum;
            delete item.laboratorio.factorRHEnum;
            let $vue = this;

            $.ajax({
                method: 'POST',
                url: APP.url('ingresante/resultadoslab/saveOtherColumns'),
                data: JSON.stringify(item.laboratorio),
                contentType: "application/json",
                success: function (response) {
                    if (response.success) {
                        console.log("response", response.data);
                        notify(response.message, 'info');
                        item.laboratorio = response.data;

                    } else {
                        notify(response.message, 'error');
                    }
                }
            });
        },

        loadTipoSangre() {
            let $vue = this;
            $.ajax({
                url: APP.url("ingresante/resultadoslab/tipoSangreList"),
                dataType: 'json',
                type: 'post',
            }).then(response => {
                console.log("tipoSangreList", response);
                $vue.tipoSangreList = response.data;
            })
        },

        loadFactorRh() {
            let $vue = this;
            $.ajax({
                url: APP.url("ingresante/resultadoslab/factorRhList"),
                dataType: 'json',
                type: 'post',
            }).then(response => {
                console.log("factorRhList", response);
                $vue.rhList = response.data;
            })
        },
        abrirObservaciones(item) {
            let $vue = this;
            $vue.itemSelected = JSON.parse(JSON.stringify(item));
            $vue.$refs.modalObservacion.open();
        },
        cerrarObservacion() {
            let $vue = this;

            $vue.$refs.modalObservacion.beginProcessing();
            $.ajax({
                method: 'POST',
                url: APP.url('ingresante/resultadoslab/saveOtherColumns'),
                data: JSON.stringify($vue.itemSelected.laboratorio),
                contentType: "application/json",
                success: function (response) {
                    $vue.$refs.modalObservacion.confirmReaction(response.success);
                    if (response.success) {
                        console.log("response", response.data);
                        notify(response.message, 'info');
                        $vue.$refs.raptorRL.loadRemoteData();
                        //item.laboratorio = response.data;

                    } else {
                        notify(response.message, 'error');
                    }
                },
                error: function () {
                    $vue.$refs.modalObservacion.confirmReaction(false);
                    notify(Messages.errorComunicacion, "error");
                }

            });

        },
        cambiarTurno(item) {
            let $vue = this;
            if ($vue.existeTurnoSelect()) {
                $vue.ingresantesURL = APP.url('ingresante/resultadoslab/list/turno/' + item.id);
            } else {
                $vue.ingresantesURL = APP.url('ingresante/resultadoslab/list/turno/0');
            }
            setTimeout(function () {
                console.log("$vue.ingresantesURL == " + $vue.ingresantesURL)
                $vue.$refs.raptorRL.loadRemoteData();
            }, 50);
        },
        clearAll() {
            let $vue = this;
            $vue.turnoSelected = {};
            $vue.ingresantesURL = APP.url('ingresante/resultadoslab/list/turno/0');
            setTimeout(function () {
                console.log("$vue.ingresantesURL == " + $vue.ingresantesURL)
                $vue.$refs.raptorRL.loadRemoteData();
            }, 50);
        },
        existeTurnoSelect() {
            let $vue = this;
            return $vue.turnoSelected.id != undefined;
        },
        verReporteTurno() {
            let $vue = this;
            if ($vue.existeTurnoSelect()) {
                let turno = $vue.turnoSelected.id;
                console.log(turno)
                location.href = APP.url('ingresante/resultadoslab/listaExcelTurno?turno=' + turno);

            } else {
                notify("Debe seleccionar un turno", "error")
            }
        },
        verObservaciones(item) {
            let $vue = this;
            $vue.itemSelected = JSON.parse(JSON.stringify(item));
            $vue.$refs.modalVerObservacion.open();
        }
    }
});







        