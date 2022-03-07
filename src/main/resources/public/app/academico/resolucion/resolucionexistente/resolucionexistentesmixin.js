var AppliedFilter = {
    data() {
        return {
            filterFacultad:null,
            visualizarSoloSeleccionados:false
        };
    },
    methods: {
        filtroFacultadSeleccionado(filtroOficina, item) {
            let $vue = this;
            if (!item.alumno) {
                return true;
            }
  
            if ($vue.visualizarSoloSeleccionados && filtroOficina) {
                return ($vue.visualizarSoloSeleccionados && (filtroOficina == item.alumno.carrera.facultad.id));
            }
            if ($vue.visualizarSoloSeleccionados) {
                return item.seleccionado;
            }
            if (filtroOficina) {
                return filtroOficina == item.alumno.carrera.facultad.id;
            }
            return true;
        },
        applyFilter(filtroOficina,visualizarSoloSeleccionados){
            this.filterFacultad=filtroOficina;
            this.visualizarSoloSeleccionados=visualizarSoloSeleccionados;
            this.$forceUpdate();
        }
    }
}