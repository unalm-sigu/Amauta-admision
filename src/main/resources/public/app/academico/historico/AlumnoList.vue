<template>
    <div>
        <section class="panel">
            <section class="panel-body">
                <raptor-table v-bind:url="alumnosURL" v-bind:preload="false" ref="load">

                    <div slot="header"></div>

                    <template scope="props" >
                        <table class="table table-striped table-hover">
                            <thead> 
                                <tr>
                                    <th class="col-md-4" colspan="2"></th>
                                    <th class="col-md-3 text-center">Programa</th>
                                    <th class="col-md-3 text-center v-middle">Situación Académica</th>
                                    <th class="col-md-1 text-center v-middle">Promedios</th>
                                    <th class="col-md-1 text-center v-middle">Estado</th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-for="item in props.data">
                                    <td class="v-middle">
                                        <div class="pull-left">
                                            <div v-if="item.persona.tipoFoto=='POSTUL' "
                                                 class="img-responsive img-thumbnail img-circle div-foto-list">
                                                <img class="img-foto-list" v-bind:src="item.persona.rutaFoto" />
                                            </div>

                                            <img v-else-if="item.persona.tipoFoto=='COMUN' " 
                                                 class="img-responsive img-thumbnail img-circle div-foto-alumno-list" 
                                                 v-bind:src="item.persona.rutaFoto" />

                                            <img v-else="" 
                                                 class="img-foto-tempo img-responsive img-thumbnail img-circle" 
                                                 v-bind:src="item.persona.rutaFoto" />
                                        </div>
                                    </td>
                                    <td class="v-middle">
                                        <a v-bind:href="urlAcademico(item)" class="block text-primary bold h5 m-b-xs m-t-xs" >
                                            {{item.persona.apellidosNombres}}
                                        </a>
                                        <span class="block">
                                            Matrícula: {{item.codigo}}
                                        </span>
                                        <small v-if="item.persona.numeroDocIdentidad != '' " class="block bold">
                                            {{item.persona.tipoDocumento.simbolo}} {{item.persona.numeroDocIdentidad}}
                                        </small>
                                    </td>

                                    <td class="v-middle text-center">
                                        <span v-if="verTipoCarrera(item)"
                                              class="block text-info bold">{{item.carrera.tipoEnum.value}} en</span>
                                        <span class="block text-success">{{item.carrera.nombre}}</span>

                                        <span v-if="verFacultad(item)" class="block">Facultad de {{item.carrera.facultad.nombre}}</span>
                                        <small class="block bold">{{item.modalidadEstudio.nombre}}</small>
                                    </td>

                                    <td class="v-middle text-center">
                                        <span class="block h5 text-primary m-t-xs m-b-xs">{{item.situacionAcademica.nombre}}</span>
                                        <span class="block"><b>Ingresó:</b> {{item.cicloIngreso.descripcion}}</span>
                                        <span v-if="item.cicloActivo"
                                              class="block"><b>Último ciclo:</b> {{item.cicloActivo.descripcion}}</span>
                                    </td>

                                    <td class="v-middle text-center">
                                        <span v-if="item.ppa" class="block"><b>ppa:</b> {{verNota(item.promedioAcumulado)}}</span>
                                        <span v-if="item.cca" class="block"><b>cca:</b> {{item.creditosCursados}}</span>
                                        <span v-if="item.capa" class="block"><b>capa:</b> {{item.creditosAprobados}}</span>
                                    </td>

                                    <td class="text-center v-middle">
                                        <span class="clear" v-if="item.estadoEnum">
                                            <a v-if="item.estado == 'ANU' "
                                               href="#" data-toggle="tooltip" data-html="true" data-placement="left" v-bind:title='item.motivo'>
                                                <span class="label label-danger" >{{item.estadoEnum.value}} </span>
                                            </a>
                                            <span v-else="" class="label label-success">{{item.estadoEnum.value}} </span>
                                        </span>
                                    </td>

                                    <td class="v-middle">
                                        <div class="actions">
                                            <a class="dropdown-toggle" href="#" data-toggle="dropdown"><i class="fa fa-cog"></i></a>
                                            <ul class="dropdown-menu pull-right">
                                                <li><a v-bind:href="urlAcademico(item)">Información académica</a></li>

                                                <li><a v-bind:href="updateAlumno(item)">Actualizar</a></li>
                                                <li><a href="#" v-on:click.prevent="eliminarAlumno(item)">Eliminar</a></li>
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
        components: {
        },
        data() {
            return {
                alumnosURL: APP.url('academico/historico/alumno/all'),
            };
        },
        mounted: function () {
            let $vue = this;
            $vue.$refs.load.repreload();
        },
        methods: {
            verTipoCarrera(item) {
                return (item.carrera.tipo == "MAE" || item.carrera.tipo == "DOC");
            },
            verFacultad(item) {
                return (item.modalidadEstudio.codigo == "PRE" && item.carrera.codigo != item.carrera.facultad.codigo);
            },
            urlAcademico(item) {
                return APP.url('academico/alumno/' + item.id + '/infoacademico') + URL_UTIL.getOrigenURL();
            },
            updateAlumno(item) {
                return APP.url('academico/historico/alumno/' + item.id + '/update') + URL_UTIL.getOrigenURL();
            },
            verNota(notax) {
                return APP.verNota(notax);
            },
            eliminarAlumno(item) {

                swal({
                    title: "Seguro que desea eliminar el registro",
                    icon: "warning",
                    buttons: ["Cancelar", "Eliminar"],
                    dangerMode: true,
                }).then((willDelete) => {
                    if (willDelete) {

                        let $vue = this;
                        axios.delete(APP.url('academico/historico/alumno/' + item.id + "/delete")).
                                then(({data}) => {
                                    notify(data, 'info');
                                    $vue.$refs.load.loadRemoteData();
                                }, () => {
                                });

                    }
                });

            }
        }
    };
</script>