<template>
    <div>

        <raptor-table v-bind:url="URL_LIST_PROFESOR" ref="load">

            <div slot="header">

                <div class="col-lg-6">

                    <multiselect v-model='departamento'
                                 label='nombre'
                                 placeholder="Todos los departamentos académicos"
                                 deselect-label=""
                                 select-label=""
                                 track-by='id'
                                 v-on:select="cambioFiltro($event)"
                                 v-on:remove="removeFiltro($event)"
                                 v-bind:allow-empty="true"
                                 v-bind:options='departamentos'>
                    </multiselect>

                </div>

            </div>

            <template scope="props" >
                <table class="table table-striped table-hover">
                    <thead> 
                        <tr>
                            <th class="col-md-3 v-middle" colspan="2"></th>                    
                            <th class="col-md-2 v-middle">Información de contacto</th>
                            <th class="col-md-2 text-center v-middle"><span>Departamento Académico</span></th>
                            <th class="col-md-2 text-center v-middle"><span>Situación laboral</span></th>
                            <th class="col-md-1 text-center v-middle">Secciones Pre.</th>   
                            <th class="col-md-1 text-center v-middle">Secciones Pos.</th>   
                            <th class="col-md-1 text-center v-middle">Estado</th>                    
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="item in props.data">
                            <td class="v-middle">
                                <div class="pull-left">
                                    <div v-if="item.tipoFoto=='POSTUL'">
                                        <a class="img-responsive img-thumbnail img-circle div-foto-list" href='#'>
                                            <img class="img-foto-list" v-bind:src="item.rutaFoto" />
                                        </a>
                                    </div>
                                    <div v-if="item.tipoFoto=='COMUN'">
                                        <img class="img-foto-tempo img-responsive img-thumbnail img-circle" v-bind:src="item.rutaFoto" />
                                    </div>
                                    <div v-else="">
                                        <img class="img-foto-tempo img-responsive img-thumbnail img-circle" v-bind:src="item.rutaFoto" />
                                    </div>
                                </div>
                            </td>
                            <td class="v-middle">
                                <div class="v-middle">
                                    <a href="#"><span class="block text-primary bold detallePostulante" >{{item.nombre}}</span></a>
                                    <span class="block">
                                        Código: {{item.codigo}} 
                                    </span>
                                    <small v-if="item.nroDocumento" class="block bold">{{item.tipoDoc}} {{item.nroDocumento}}</small>
                                </div>
                            </td>
                            <td class="v-middle">
                                <span v-if="item.emailEmpresa" class="block"><i class="fa fa-envelope text-primary"></i> {{item.emailEmpresa}}</span>
                                <span v-if="item.email" class="block"><i class="fa fa-envelope-o"></i> {{item.email}}</span>
                                <span v-if="item.celular" class="block"><i class="fa fa-mobile"></i> {{item.celular}}</span>
                                <span v-if="item.telefono" class="block"><i class="fa fa-phone"></i> {{item.telefono}}</span>
                            </td>
                            <td class="text-center v-middle">
                                <span class="block text-primary">{{item.departamentoAcademico}}</span>
                                <small class="block">Facultad de {{item.facultad}}</small>
                            </td>
                            <td class="text-center v-middle">{{item.situacion}}</td>
                            <td class="v-middle">
                                <label  >{{item.cantSeccionesPre}}</label>
                            </td>
                            <td class="v-middle">
                                <label  >{{item.cantSeccionesPos}}</label>
                            </td>
                            <td class="text-center v-middle">
                                <label v-if="item.estado=='INA'" class="label label-default" >Inactivo</label>
                                <label v-if="item.estado=='ACT'" class="label label-primary" >Activo</label>
                            </td>
                            <td class="v-middle">
                                <div class="actions">
                                    <a class="dropdown-toggle" href="#" data-toggle="dropdown"><i class="fa fa-cog"></i></a>
                                    <ul class="dropdown-menu pull-right" >
                                        <li v-if="item.estado == 'INA'" role="presentation">
                                            <a v-if="PUEDE_ACTIVAR" class="btn-link estado" v-on:click.prevent='activarDocente(item)' href="#">Activar</a>
                                        </li>
                                        <li><a class="btn-link modificar"  v-bind:href="urlInfAcademica(item)">Información</a></li>
                                    </ul>
                                </div>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </template>
        </raptor-table>

    </div>
</template>

<script>
    module.exports = {
        data() {
            return {
                URL_LIST_PROFESOR: APP.url('academico/profesor/all'),
                PUEDE_ACTIVAR: PUEDE_ACTIVAR,
                departamentos: JSON.parse(jDepartamentos),
                departamento: null
            };
        },
        mounted: function () {
            let $vue = this;
            console.log('lista');
        },
        methods: {
            urlInfAcademica(item) {
                return APP.url("academico/profesor/" + item.id + "/informacionacademica")
            },
            activarDocente(item) {
                let $vue = this;
                let formDocente = {};
                formDocente.id = item.id;
                formDocente.estado = item.estado;

                var mimodal = bootbox.confirm({
                    title: "Cambiar Estado",
                    message: '¿Seguro que desea cambiar el estado del docente?',
                    buttons: {
                        confirm: {label: "Sí, aceptar", className: "btn-info"},
                        cancel: {label: "Cancelar", className: "btn-link"}
                    },
                    callback: function (result) {
                        if (result) {
                            $.ajax({
                                url: APP.url('academico/profesor/estado'),
                                type: 'POST',
                                data: formDocente,
                                success: function (response) {

                                    if (response.success) {

                                        $vue.$refs.load.loadRemoteData();
                                        notify(response.message, "success");
                                        mimodal.modal('hide');

                                    } else {
                                        notify(response.message, "error");
                                    }
                                },
                                error: function () {
                                    mimodal.modal('hide');
                                    notify(Messages.errorComunicacion, "error");
                                }
                            });
                        } else {
                            mimodal.modal('hide');
                        }
                        return false;
                    }
                });
            },
            cambioFiltro($event) {
                let $vue = this;
                $vue.$refs.load.querie.push({name: 'departamento', value: $event.id});
                $vue.$refs.load.loadRemoteData();
            },
            removeFiltro() {
                let $vue = this;
                $vue.$refs.load.querie.push({name: 'departamento', value: null});
                $vue.$refs.load.loadRemoteData();
            }
        }
    };
</script>