<template>
  <modal-vik ref="modalAddAlumnoBorrado"
             v-bind="modalAddAlumnoBorrado"
             v-bind:okaction="saveAddAlumnoBorrado">
    <div slot="body">

      <h4 class="text-primary block m-b-lg"> {{ title }} </h4>

      <form v-bind:id="form">
        <template>
          <div class="form-group">
            <label>Alumno</label>

            <multiselect v-model="registroBorradoAlumno.alumno"
                         v-bind:options="alumnos"
                         v-bind:allow-empty="false"
                         v-on:search-change="searchAlumno"
                         v-on:input="selectAlumno"
                         track-by="id"
                         placeholder="Seleccione un alumno"
                         v-bind:internal-search="false"
                         v-bind:showNoOptions="true"
                         v-bind:show-labels="false">

              <template slot="singleLabel" slot-scope="props">
                <span class="bold">{{ props.option.codigo }} {{ props.option.persona.apellidosNombres }}</span> -
                <span class="text-primary bold">Ingreso: {{ props.option.cicloIngreso.descripcion }}</span> -
                <span class="text-primary bold">Sit. Académica: {{ props.option.situacionAcademica.nombre }}</span>
              </template>

              <template slot="option" slot-scope="props">
                <span class="block bold">{{ props.option.persona.apellidosNombres }} </span>
                <span class="block text-xs">{{ props.option.codigo }} - {{ props.option.carrera.nombre }}</span>
                <span class="block text-xs">
                  {{ props.option.persona.tipoDocumento.simbolo }} - {{
                    props.option.persona.numeroDocIdentidad
                  }}</span>
                <span class="block text-xs">Ciclo Ingreso: {{ props.option.cicloIngreso.descripcion }}</span>
                <span class="block text-xs">Situación Académica: {{ props.option.situacionAcademica.nombre }}</span>
              </template>

              <template slot="noOptions">Lista vacía</template>
              <template slot="noResult">Sin resultados</template>
            </multiselect>
            <input v-bind:value="getObjectId(registroBorradoAlumno.alumno)" required="true" type="text" class="hide"/>

          </div>


          <div class="form-group">
            <label>Ciclo</label>

            <multiselect v-model="registroBorradoAlumno.cicloAfectado"
                         v-bind:options="ciclosEstudiados"
                         v-bind:allow-empty="false"
                         v-on:input="selectCiclo"
                         track-by="id"
                         placeholder="Seleccione un ciclo"
                         v-bind:internal-search="false"
                         v-bind:showNoOptions="true"
                         v-bind:show-labels="false">

              <template slot="singleLabel" slot-scope="props">
                <span class="bold">{{ props.option.descripcion2 }}</span>
              </template>

              <template slot="option" slot-scope="props">
                <span class="bold">{{ props.option.descripcion }} - {{ props.option.descripcion2 }}</span>
              </template>

              <template slot="noOptions">Lista de ciclos vacio</template>
              <template slot="noResult">Sin resultados</template>
            </multiselect>
            <input v-bind:value="getObjectId(registroBorradoAlumno.cicloAfectado)" required="true" type="text"
                   class="hide"/>
          </div>

          <div class="form-group">
            <label>Motivo</label>
            <textarea class="form-control" v-model="registroBorradoAlumno.motivo" required="true">
            </textarea>
          </div>

          <template v-if="infoHistorial">
            <div class="form-group">
              <table class="table table-hover">
                <thead>
                <tr>
                  <th class="col-md-9">Curso</th>
                  <th class="col-md-2">Nota</th>
                  <th class="col-md-1 text-left">Estado</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="(item, index) in infoHistorial.alumnoCicloCursos">

                  <td class="v-middle">
                    <span class="block text-primary">{{ item.curso.codigo }} {{ item.curso.nombre }}</span>
                    <span class="block"></span>
                  </td>
                  <td class="v-middle">
                    <span class="block text-primary">{{ item.nota }}</span>
                    <span class="block"></span>
                  </td>
                  <td class="v-middle">
                    <div v-bind:class="style(item.estado)">
                      {{ item.estadoEnum.value }}
                    </div>
                  </td>

                </tr>
                </tbody>
              </table>
            </div>

          </template>


        </template>
      </form>
    </div>
  </modal-vik>
</template>

<script>

module.exports = {

  data() {
    return {
      registroBorradoAlumno: {alumno: null,},
      raptor: null,
      ciclosEstudiados: [],
      alumnos: [],
      infoAlumno: {alumno: '', cicloAfectado: ''},
      infoHistorial: {alumnoCicloCursos: []},
      form: "id-form-add-alumno",
      title: "Agregar alumno",
      modalAddAlumnoBorrado: VUE_MODAL.structFormAjax({
        id: "id-modal-add-alumno",
        okbtn: "Agregar alumno",
        okclass: "btn-primary"
      })
    };
  },
  methods: {
    open(raptor) {
      var form = $("#" + this.form);
      form.parsley().destroy();

      this.raptor = raptor;
      this.registroBorradoAlumno = {alumno: null, cicloAfectado: null};
      this.infoAlumno = null;
      this.infoHistorial = {alumnoCicloCursos: []};
      ;
      this.ciclosEstudiados = [];
      this.alumnos = [];
      this.$refs.modalAddAlumnoBorrado.open();
    },
    searchAlumno(nombre) {
      this.ciclosEstudiados = [];
      if (nombre) {
        myUtils.axios(VUE_AXIOS.structGetData({
          url: `/academico/registroborradoalu/alumnosByFilter?filter=${nombre}`
        })).then((resp) => this.alumnos = resp.data.data);
      }
    },
    selectAlumno(item) {
      this.registroBorradoAlumno.cicloAfectado = null;

      this.ciclosEstudiados = [];
      this.infoHistorial = {alumnoCicloCursos: []};
      this.$nextTick(() => {
        this.getListaciclos(item);
      });

    },
    selectCiclo(item) {
      this.infoHistorial = {alumnoCicloCursos: []};
      this.registroBorradoAlumno.motivo = '';

      this.infoAlumno = {idAlumno: this.registroBorradoAlumno.alumno.id, idCicloEstudiado: item.id};
      this.getInfoAcademico(this.infoAlumno);
      this.$nextTick(() => {
        $("#" + this.form).parsley().validate();
      });
    },
    getListaciclos(item) {

      myUtils.axios(VUE_AXIOS.structGetData({
        url: `/academico/registroborradoalu/getCiclosEstudiados`,
        body: item
      })).then((resp) => this.ciclosEstudiados = resp.data.data);
    },
    getInfoAcademico(infoAlumno) {
      myUtils.axios(VUE_AXIOS.structGetData({
        url: `/academico/registroborradoalu/getInfoacademico`,
        body: infoAlumno
      })).then((resp) => this.infoHistorial = resp.data.data || {alumnoCicloCursos: []});

    },
    saveAddAlumnoBorrado() {
      var form = $("#" + this.form);
      if (!form.parsley().validate()) {
        return;
      }

      let payload = this.registroBorradoAlumno;

      myUtils.axios(VUE_AXIOS.structModalClose({
        url: `/${rutaModulo}/save`,
        modal: this.$refs.modalAddAlumnoBorrado,
        raptor: this.raptor,
        body: payload
      }));
    },
    style(item) {
      var colorEstado = {MAT: 'success', PMAT: 'warning', NMAT: 'default'};
      var res = colorEstado[item];
      if (res === undefined) {
        return "label label-danger";
      }
      return "label label-" + res;
    },

    // metodos genericos
    getObjectId: myUtils.getObjectId,
    getObjectName: myUtils.getObjectName,
    commas: myUtils.commas
  }
};
</script>