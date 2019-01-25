new Vue({
    el: '#ingresantesVUE',
    data: {
        ingresantesURL: APP.url('ingresante/hojarecorrido/list'),
    },
    mounted: function () {
        let $vue = this;
    },
    methods: {
        verDetalle(item, act) {
            var texto = act.tipoActividadIngresante.codigoOficina == 'ALUMNO' ? 'Alumno' : act.tipoActividadIngresante.codigoOficina;
            texto += ' - ' + act.tipoActividadIngresante.nombre;
            var time = 0;
            for (var i = 0; i < texto.length + 1; i++) {
                setTimeout(function () {
                    if (!item.ocultar) {
                        item.descripcion = texto.substring(0, time);
                        time++;
                    }
                }, i * 15)
            }
            item.ocultar = false;
        },
        noverDetalle(item) {
            item.ocultar = true;
        },
        actualizar() {
            let $vue = this;
            $vue.$refs.raptorIng.loadRemoteData();
        }
    }
});







        