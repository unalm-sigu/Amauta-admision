Vue.component("multiselect", window.VueMultiselect.default);

new Vue({
    el: '#ReporteHorarioVUE',
    data: {
        ReporteHorarioProgramacionURL: APP.url('reporte/programacion/lisReporte'),
        ciclo: JSON.parse(cicloJson),
        colorEstado: {ACT: 'success', INA: 'danger', CRE: "default"},
        colorEstadoGpos: {COMP: 'success', INCOMP: 'danger'}
    },
    methods: {
        reporteProgramacionHoario(item) {
            var vue = this;
//            var downloadWindow = window.open("", "_blank");
           $.fileDownload("/reporte/programacion/ReporteHorario", {          
                httpMethod: "POST",
                data: {facultad: item.codigo},
                successCallback: function (responseHtml, url) {
//                    downloadWindow.close();
                },
                onFail: function (e) {
                    console.log(e);
                },
                failCallback: function (responseHtml, url) {
                    notify(Messages.errorComunicacion, 'error')
                }
            });

        },
    }
});
