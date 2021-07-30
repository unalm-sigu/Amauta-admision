<template>
    <div>

        <section class="panel">
            <section class="panel-body">
                <raptor-table ref="cambioplancurriculartable"
                              v-bind:url="URL_TRAMITE_CAMBIO_PLAN_CURRICULAR" >
                    <template scope="props">
                        <table class="table table-striped">
                            <thead>
                                <tr>
                                    <th class="col-sm-3">Trámite</th>
                                    <th class="col-sm-6">Persona</th>
                                    <th class="col-sm-3">Resolución</th>
                                    <th class="col-sm-1">Estado</th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-for="item in props.data"> 
                                    <td class="v-middle"> 
                                        <span class="block">Serie: {{item.tramite.serie}}</span>
                                        <span class="block">Número: {{item.tramite.numero}}</span>
                                        <span class="block">Ciclo Readmisión: {{item.cicloReadmitido.descripcion}}</span>

                                    </td>
                                    <td class="v-middle text-left">
                                        <div class="bock " >
                                            {{ item.tramite.persona.apellidosNombres}}
                                        </div>
                                        <div class="bock bold text-primary">
                                            {{ item.tramite.alumno.codigo}}
                                        </div>
                                        <div class="bock " v-if="item.tramite.alumno.carrera">
                                            <span class="">Facultad de </span>
                                            {{ item.tramite.alumno.carrera.facultad.nombre}}
                                        </div>
                                    </td>
                                    <td class="v-middle ">
                                        <a v-if="item.resolucion" class="text-success" > {{ item.resolucion.descripcion}}</a>
                                    </td>
                                    <td class="v-middle text-center">
                                        <span class="" v-bind:class="labelColor(item.tramite.estadoEnum.name)"> {{ item.tramite.estadoEnum.value}}</span>
                                    </td>

                                    <td class="v-middle">
                                        <div v-if="item.estado!='ANU'" class="dropdown actions" >
                                            <a class="dropdown-toggle" data-toggle="dropdown">
                                                <i class="fa fa-cog"></i>
                                            </a> 
                                            <ul class="dropdown-menu pull-right" > 
                                                <li><a v-bind:href="urlAcademico(item)">Información académica</a></li>
                                                <li><a href="#" v-on:click.prevent="getReporte(item)">Reporte</a></li>
                                                <li role="presentation" class="divider"></li>
                                                <li><a href="#" v-on:click.prevent="anularTramite(item)">Anular</a></li>
                                            </ul>
                                        </div>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </template>
                </raptor-table>

            </section>
        </section>

    </div>
</template>

<script>
    module.exports = {
        data() {
            return {
                URL_TRAMITE_CAMBIO_PLAN_CURRICULAR: APP.url('academico/tramiteacademico/cambioplancurricular/all'),
            };
        },
        mounted: function () {
            let $vue = this;
        },
        methods: {
            labelColor(item) {
                switch (item) {
                    case  "SOL":
                        return "label label-success"
                        break;
                    case  "ANU":
                        return "label label-danger"
                        break;
                    default :
                        return "label label-primary"
                        break;
                }
            },
            getReporte(item) {
                axios_blob.get(APP.url('academico/tramiteacademico/cambioplancurricular/' + item.id + '/reporte'))
                        .then(response => {
                            UTIL_BLOB.save(response);
                        }, () => {
                            notify(Messages.errorComunicacion, 'error')
                        });
            },
            urlAcademico(item) {
                return APP.url('academico/alumno/' + item.tramite.alumno.id + '/infoacademico') + URL_UTIL.getOrigenURL();
            },
            recargar() {
                this.$refs.cambioplancurriculartable.loadRemoteData();
            },
            anularTramite(item) {
                let $vue = this;
                swal({
                    title: "Seguro que desea anular el registro",
                    icon: "warning",
                    buttons: ["Cancelar", "Anular"],
                    dangerMode: true,
                }).then((willDelete) => {
                    if (willDelete) {

                        axios.get(APP.url('academico/tramiteacademico/cambioplancurricular/' + item.id + '/anular')).
                                then(({data}) => {
                                    if (data.success) {
                                        notify(data.message, 'info');
                                        $vue.recargar();
                                    } else {
                                        notify(data.message, "error");
                                }
                                }, error => {
                                    notify(Messages.errorComunicacion, "error");
                                });

                    }
                });
            }
        }
    };
</script>