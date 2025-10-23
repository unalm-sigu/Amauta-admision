<template>
  <div>

    <raptor-table v-bind:url="URL_LIST_PROFESOR" ref="load">

      <div slot="header">

        <div class="col-lg-4">
          <multiselect v-model='departamento'
                       label='nombre'
                       placeholder="Todos los departamentos académicos"
                       deselect-label=""
                       select-label=""
                       track-by='id'
                       v-on:select="cambioFiltroDepartamento($event)"
                       v-on:remove="removeFiltroDepartamento($event)"
                       v-bind:allow-empty="true"
                       v-bind:options='departamentos'>
          </multiselect>
        </div>

        <div class="col-lg-3">
          <multiselect v-model='tipoPrograma'
                       label='nombre'
                       placeholder="Pregrado y Postgrado"
                       deselect-label=""
                       select-label=""
                       track-by='codigo'
                       v-on:select="cambioFiltroPrograma($event)"
                       v-on:remove="removeFiltroPrograma($event)"
                       v-bind:allow-empty="true"
                       v-bind:options='tiposPrograma'>
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
            <th class="col-md-3 text-center v-middle"><span>Situación laboral</span></th>
            <th class="col-md-1 text-center v-middle">Estado</th>
            <th></th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="item in props.data">
            <td class="v-middle">
              <div v-if="item.foto">
                <img class="img-foto-tempo img-responsive img-thumbnail  img-circle" v-bind:src="item.foto" />
              </div>
              <div v-else="">
                <img v-if="item.sexo == 'M'" class="img-foto-tempo img-responsive img-thumbnail img-circle" :src="'/phobos/images/unalm/male.png'" />
                <img v-else-if="item.sexo == 'F'" class="img-foto-tempo img-responsive img-thumbnail img-circle" :src="'/phobos/images/unalm/female.png'" />
                <img v-else class="img-foto-tempo img-responsive img-thumbnail img-circle" :src="'/phobos/images/unalm/unknown-person.gif'" />
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

              <a v-if="item.emailEmpresa && LOGIN_DOCENTE" :class="['btn', 'm-t-sm', IS_PRODUCTION ? 'btn-success' : 'btn-warning']" v-on:click="loginAmauta(item)">
                Login amauta
              </a>
            </td>
            <td class="text-center v-middle">
              <span class="block text-primary">{{item.departamentoAcademico}}</span>
              <small class="block">Facultad de {{item.facultad}}</small>
            </td>
            <td class="v-middle">
              <span class="block">
                  <span class="text-primary">Situación:</span>
                  <span class="text-dark"> {{ item.situacion }}</span>
              </span>
              <span class="block">
                  <span class="text-primary">Categoría:</span>
                  <span class="text-dark"> {{ item.categoria }}</span>
              </span>
              <span class="block">
                  <span class="text-primary">Dedicación:</span>
                  <span class="text-dark"> {{ item.dedicacion }}</span>
              </span>
            </td>

            <td class="text-center v-middle">
              <label v-if="item.estado=='INA'" class="label label-default" >Inactivo</label>
              <label v-if="item.estado=='ACT'" class="label label-primary" >Activo</label>
            </td>
            <td class="v-middle">
              <div class="actions">
                <a class="dropdown-toggle" href="#" data-toggle="dropdown"><i class="fa fa-cog"></i></a>
                <ul class="dropdown-menu pull-right" >
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
  components: {
    RaptorTable: use("/_vue/modules/RaptorTable.vue"),
  },
  data() {
    return {
      URL_LIST_PROFESOR: APP.url('academico/docente/carga/all'),
      PUEDE_ACTIVAR: PUEDE_ACTIVAR,
      LOGIN_DOCENTE: LOGIN_DOCENTE,
      IS_PRODUCTION: IS_PRODUCTION,
      departamentos: JSON.parse(jDepartamentos),
      departamento: null,
      tipoPrograma: null,
      tiposPrograma: [
        { codigo: 'PRE', nombre: 'Pregrado' },
        { codigo: 'POS', nombre: 'Postgrado' }
      ]
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
    cambioFiltroDepartamento($event) {
      let $vue = this;
      $vue.$refs.load.querie.push({name: 'departamento', value: $event.id});
      $vue.$refs.load.loadRemoteData();
    },
    removeFiltroDepartamento() {
      let $vue = this;
      $vue.$refs.load.querie.push({name: 'departamento', value: null});
      $vue.$refs.load.loadRemoteData();
    },
    cambioFiltroPrograma($event) {
      let $vue = this;
      $vue.$refs.load.querie.push({name: 'tipoPrograma', value: $event.codigo});
      $vue.$refs.load.loadRemoteData();
    },
    removeFiltroPrograma() {
      let $vue = this;
      $vue.$refs.load.querie.push({name: 'tipoPrograma', value: null});
      $vue.$refs.load.loadRemoteData();
    },
    loginAmauta(item) {
      let $vue = this;
      location.href = `/bunnies/${item.emailEmpresa}`
    }
  }
};
</script>