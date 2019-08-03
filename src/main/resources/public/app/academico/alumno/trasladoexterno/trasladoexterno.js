Vue.component("multiselect", window.VueMultiselect.default);
new Vue({
    el: '#convalTrasladoExterno',
    data: {
        alumno: JSON.parse(alumnoJson),
        ciclo: JSON.parse(cicloJson),
        listTramiteTraslado: JSON.parse(listTramiteTrasladoJson),
        listAlumnoCursoCurricula: JSON.parse(listAlumnoCursoCurriculaJson),
        listCursoConvalidado: JSON.parse(listCursoConvalidadoJson),
        listAlumnoCursoCOptions: [],
        almCursCurricula: null,
        tramiteTrasladoActivo: null,
        total: 0
    },
    mounted: function () {
        let $vue = this;
        $vue.updateListOptions();
        $vue.countTotal();
        $vue.findTramiteTrasladoActivo();
    },
    methods: {
        customLabel(item) {
            return item.curso.nombre + " - " + item.curso.codigo + " Nro Ciclo " + item.numeroCiclo;
        },
        returnEstado(estado) {
            return estado === 'ACT' ? 'Activo' : 'Inactivo';
        },
        countTotal() {
            let $vue = this;
            for (var i = 0; i < $vue.listCursoConvalidado.length; i++) {
                $vue.updateTotalCreditos($vue.listCursoConvalidado[i], "add");
            }
        },
        returnTipoCurso(tipo) {

            if (tipo === 'TEO') {
                return "Teoría"
            }
            if (tipo === 'TEOPRA') {
                return "Teoría y Práctica"

            }
            if (tipo === 'PRA') {
                return "Práctica"

            }
        },
        listUpdate(item) {
            let $vue = this;
            for (var i = 0; i < $vue.listAlumnoCursoCOptions.length; i++) {
                if ($vue.listAlumnoCursoCOptions[i].id === item.id) {
                    $vue.listAlumnoCursoCOptions.splice(i, 1);
                }
            }
        }
        ,
        updateListOptions() {
            let $vue = this;
            $vue.listAlumnoCursoCOptions = [];
            $vue.listAlumnoCursoCOptions = Object.assign([], $vue.listAlumnoCursoCurricula);
            for (var j = 0; j < $vue.listCursoConvalidado.length; j++) { // cursos seleccionados
                for (var i = 0; i < $vue.listAlumnoCursoCOptions.length; i++) {
                    if ($vue.listAlumnoCursoCOptions[i].curso.id === $vue.listCursoConvalidado[j].curso.id) {
                        $vue.listAlumnoCursoCOptions.splice(i, 1); //optiones a no mostar en el multiselect
                    }
                }
            }
        },
        addCurso() {
            let $vue = this;
            if ($vue.almCursCurricula === null) {
                notify("Debe seleccionar un curso para agregar.", "warning")
                return;
            }
            let objectClone = Object.assign({}, $vue.almCursCurricula);
            $vue.listUpdate(objectClone);
            $vue.listCursoConvalidado.push({id: null, curso: objectClone.curso, tramiteTraslado: {id: $vue.tramiteTrasladoActivo.id, alumno: $vue.alumno}});
            $vue.updateTotalCreditos($vue.almCursCurricula, "add");
            $vue.almCursCurricula = null;
        },
        deleteItem(index, item) {
            let $vue = this;
            $vue.updateTotalCreditos(item, "remove");
            $vue.listCursoConvalidado.splice(index, 1);
            $vue.updateListOptions();

        },
        updateTotalCreditos(item, param) {
            let $vue = this;
            if (param === "add") {
                $vue.total = $vue.total + (item.curso.creditos);
            }

            if (param === "remove") {
                $vue.total = $vue.total - (item.curso.creditos);
            }
        },
        findTramiteTrasladoActivo() {
            let $vue = this;
            for (var i = 0; i < $vue.listTramiteTraslado.length; i++) {
                if ($vue.listTramiteTraslado[i].estado == 'ACT') {
                    $vue.tramiteTrasladoActivo = $vue.listTramiteTraslado[i];
                }
            }
        },
        save() {
            let $vue = this;
            let list = [];

            for (var i = 0; i < $vue.listCursoConvalidado.length; i++) {
                if ($vue.listCursoConvalidado[i].id === null) {
                    list.push($vue.listCursoConvalidado[i]);
                }
            }

            if (list.length === 0) {
                notify("Debe agregar almenos un curso para convalidar.", "warning");
                return;
            }

            let trasladoBean = {listCursoConvalidado: list, total: $vue.total, alumno: $vue.alumno, tramiteTraslado: $vue.tramiteTrasladoActivo};

            axios.post("/" + rutaModulo + "/saveListCursoConvalidado", trasladoBean)
                    .then(response => {
                        if (response.data.success) {
                            notify(response.data.message, "success");
                        } else {
                            notify(response.data.message, "warning");
                        }
                    }).catch(e => {
                notify(MESSAGES.errorComunicacion, "error");
            });
        }
    }
});
