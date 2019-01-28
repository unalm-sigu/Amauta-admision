
new Vue({
    el: '#main',
    data: {
        rolExamen: [],
        horario: {
            id: 'horario',
            header: true,
            title: 'Horario',
            modalsize: 'modal-lg'
        },
        semanasExamen: [],
        grupoHorasExamen: [],
        semanaExamenActiva: null,
        grupoActivo: null,
    },
    mounted() {
        this.allRolExamen();

    },
    methods: {
        allRolExamen() {
            let $vue = this;
            $.ajax({
                method: "POST",
                url: APP.url("rolexamen/docentes/list"),
                contentType: "application/json",
            }).then(response => {
                if (response.success) {
                    $vue.rolExamen = response.data;
                    console.log($vue.rolExamen);
                    this.plantilla();

                } else {
                    notify(response.message, 'error');
                }
            }, error => {
                notify(MESSAGES.errorComunicacion, 'error');
            });

        },
        plantilla: function () {
            let $vue = this;
            $vue.rolExamen.forEach(function (item) {
                $vue.grupoHorasExamen.push(item.grupoHorasExamen);
            })

            $vue.grupoHorasExamen = Array.from(new Set($vue.grupoHorasExamen.map(s => s.id)))
                    .map(id => {
                        return {
                            id: id,
                        };
                    });
            var data = {grupoHorasExamens: $vue.grupoHorasExamen};
            var id = $vue.rolExamen[0].idRolExamen;
            AXIOS.post("docentes/plantilla/" + id, data)
                    .then(response => {
                        if (response.data.success) {
                            this.semanasExamen = response.data.data;
//                            if ($vue.semanaExamenActiva != null) {
//                                $vue.seleccionarSemana(this.semanaExamenActiva);
//                            } else {
//                                $vue.seleccionarSemana(this.semanasExamen[0]);
//                            }
                        }
                    });


        },
        model(grupoId) {
            let $vue = this;
            $vue.grupoActivo = {id: grupoId};
            $vue.$refs.horario.open();
            $vue.seleccionarSemana();
        },
        seleccionarSemana() {
            let $vue = this;
            this.semanasExamen.forEach(function (x) {
                x.selected = false;
            });
            this.semanasExamen.forEach(function (x) {
                var values = Object.values(x.tblHorarioSeamanaExamen.fechasHorasGrupos);
                values.forEach(function (y) {
                    if (y.grupoHorasExamen.id == $vue.grupoActivo.id) {
                        x.selected = true;
                        $vue.semanaExamenActiva = x;
                    }
                })
            });
//            for (var x in this.semanasExamen) {
//                var values = Object.values(x.tblHorarioSeamanaExamen.fechasHorasGrupos);
//                for (var y in values) {
//                    if (y.grupoHorasExamen.id == $vue.grupoActivo.id) {
//                        x.selected = true;
//                        $vue.semanaExamenActiva = x;
//                        break;
//                    } else {
//                        x.selected = false;
//                    }
//                }
//            }
        },
        fechaGrupoHoraItem(fechaGrupoHora) {
            if (this.grupoActivo != null && fechaGrupoHora.grupoHorasExamen.id == this.grupoActivo.id) {
                return "border-color:#600D63; background-color:#DCDFE3;color:#000000;"
            }

            return "border-color:#DFE7EE; background-color:#FFFFFF;color:#E40DEB;"
        }
    }
});
