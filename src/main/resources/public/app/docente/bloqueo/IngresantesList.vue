<template>
    <div>

        <section class="panel">
            <section class="panel-body">
                <raptor-table v-bind:url="URL_BLOQUEO_INGRESANTES" ref="load">
                    <div slot="header">

                        <div class="col-lg-6">

                            <multiselect v-model='matricula'
                                         v-bind:options='filtros'
                                         label='nombre'
                                         placeholder="Aprobados y Desaprobados"
                                         deselect-label=""
                                         select-label=""
                                         track-by='id'
                                         v-on:select="cambioFiltro($event)"
                                         v-on:remove="removeFiltro($event)"
                                         v-bind:allow-empty="true">
                            </multiselect>

                        </div>

                    </div>

                    <template scope="props">
                        <table class="table table-striped">
                            <thead>
                                <tr>
                                    <th class="col-md-4 text-left v-middle">Carrera</th>
                                    <!--<th class="col-md-2 text-left v-middle">Alumno</th>-->
                                    <th class="col-md-1 text-left v-middle">RM</th>
                                    <th class="col-md-1 text-left v-middle">RV</th>
                                    <th class="col-md-1 text-left v-middle">Matemática</th>
                                    <th class="col-md-1 text-left v-middle">Fisica</th>
                                    <th class="col-md-1 text-left v-middle">Quimica</th>
                                    <th class="col-md-1 text-left v-middle">Biologia</th>
                                    <th class="col-md-1 text-left v-middle">Inscripción</th>
                                    <th class="col-md-1 text-left v-middle">Matricular</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-for="item in props.data"> 
                                    <!--                                    <td class="v-middle">
                                                                            <span class="bold">{{item.ingresante.carrera.nombre}}</span> 
                                                                        </td>-->
                                    <td class="v-middle">
                                        <div class="v-middle">
                                            <span class="block" >
                                                <span v-bind:class=" { 'bold text-success': item.matricula , 'bold text-danger': !item.matricula }" v-text="item.ingresante.postulante.persona.apellidosNombres"></span>
                                            </span>
                                            <span class="block" >
                                                <span class="bold">Matrícula:</span> {{item.ingresante.codigo}}
                                            </span>
                                            <span class="block" >
                                                <span class="bold"><i class="fa fa-phone" aria-hidden="true"></i></span>  {{item.ingresante.postulante.persona.telefono}}
                                                <smal class="bold">&nbsp;|&nbsp; </smal>
                                                <span class="bold"><i class="fa fa-mobile" aria-hidden="true"></i></span>  {{item.ingresante.postulante.persona.celular}}
                                            </span>
                                            <span class="block" >
                                                <span class="bold"><i class="fa fa-envelope-o" aria-hidden="true"></i></span> {{item.ingresante.postulante.persona.email}}
                                            </span>
                                            <span class="block" >
                                                <span class="bold"><i class="fa fa-envelope" aria-hidden="true"></i></span> {{item.ingresante.postulante.persona.emailCompania}}
                                            </span>
                                            <span class="block" >
                                                <span class="bold">Carrera: </span> {{item.ingresante.carrera.nombre}}
                                            </span>
                                            <span class="block" >
                                                <span class="bold">Mod.Ingreso: </span> {{item.ingresante.postulante.modalidadIngreso.nombre}}
                                            </span>
                                        </div>
                                    </td>



                                    <td class="v-middle ">
                                        <span  v-bind:class="getClass(item.rm)">{{item.rm}}</span> 
                                        <!--<span>Fin: {{item.rm}}</span>--> 
                                    </td>
                                    <td class="v-middle ">
                                        <span v-bind:class="getClass(item.rv)"> {{item.rv}}</span> 
                                    </td>
                                    <td class="v-middle ">
                                        <span v-bind:class="getClass(item.matematica)"> {{item.matematica}}</span> 
                                    </td>
                                    <td class="v-middle ">
                                        <span v-bind:class="getClass(item.fisica)"> {{item.fisica}}</span> 
                                    </td>
                                    <td class="v-middle ">
                                        <span v-bind:class="getClass(item.quimica)"> {{item.quimica}}</span> 
                                    </td>
                                    <td class="v-middle">
                                        <span v-bind:class="getClass(item.biologia)"> {{item.biologia}}</span> 
                                    </td>
                                    <td class="v-middle ">
                                        <a v-if="item.inscrito" class="text-center text-success">
                                            <i class="fa fa-check-circle fa-3x" aria-hidden="true"></i>
                                        </a>
                                        <a v-else="item.inscrito" class="text-center text-danger" >
                                            <i class="fa fa-times-circle fa-3x" aria-hidden="true"></i>
                                        </a>
                                    </td>
                                    <td class="v-middle ">
                                        <a v-if="item.matricula" class="text-center text-success">
                                            <i class="fa fa-check-circle fa-3x" aria-hidden="true"></i>
                                        </a>
                                        <a v-else="item.matricula" class="text-center text-danger pointer" v-on:click.prevent="actualizar(item)">
                                            <i class="fa fa-times-circle fa-3x" aria-hidden="true"></i>
                                        </a>
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

            RaptorTable: use("/_vue/modules/RaptorTable.vue"),
        },
        data() {
            return {
                URL_BLOQUEO_INGRESANTES: APP.url('docente/matricula/bloqueo/all'),
                filtros: [{id: 1, nombre: 'Aprobados'}, {id: 0, nombre: 'Desaprobados'}],
                matricula: null
            };
        },
        mounted: function () {
            let $vue = this;
        },
        methods: {
            actualizar(item) {
                let $vue = this;
                var alumno = item.ingresante.postulante.persona.paterno + ' '
                        + item.ingresante.postulante.persona.materno + ', '
                        + item.ingresante.postulante.persona.nombres;

                swal('¿Desea dar acceso alumno ' + alumno + '?', {
                    icon: "warning",
                    closeOnClickOutside: false,
                    closeOnEsc: false,
                    dangerMode: true,
                    buttons: {
                        cancel: {text: "Cancelar", closeModal: true, visible: true},
                        confirm: {text: "Sí, actualizar", closeModal: false}
                    }
                }).then((value) => {
                    if (value != true) {
                        return;
                    }
                    axios_.put("/docente/matricula/bloqueo/actualizar/" + item.id)
                            .then(({data}) => {
                                if (data.success) {
                                    $vue.reload();
                                    notify('Se dio acceso al ingresante', 'success');
                                    swal.close();
                                } else {
                                    notify('Error al dar acceso', 'error');
                                    swal.close();
                            }

                            }, () => {
                                return swal(APP.errorComunicacion, "error");
                            });

                }).catch(err => {
                    if (err) {
                        swal(APP.errorComunicacion, "error");
                    } else {
                        swal.stopLoading();
                        swal.close();
                    }
                });
            },
            cambioFiltro($event) {
                let $vue = this;
                $vue.$refs.load.querie.push({name: 'matricula', value: $event.id});
                $vue.$refs.load.loadRemoteData();
            },
            removeFiltro() {
                let $vue = this;
                $vue.$refs.load.querie.push({name: 'matricula', value: null});
                $vue.$refs.load.loadRemoteData();
            },
            reload() {
                let $vue = this;
                $vue.$refs.load.loadRemoteData();
            }, getClass(nota) {
                if (nota >= 10.5) {
                    return 'bold text-success';
                } else {
                    return 'bold text-danger';
                }
            }
        }
    };
</script>