<template>
  <v-container>
    <v-form v-model="valid" ref="form" lazy-validation>
    <v-layout
      text-xs-center
      wrap
    >
      <v-flex mb-4>
        <h1 class="display-2 font-weight-bold mb-3">
          Welcome to Kalaha
        </h1>
        <p class="subheading font-weight-regular">
          Create a new game or join an existing game
        </p>
      </v-flex>

      <v-flex
        mb-5
        xs12
      >
        <v-layout justify-center>
          <v-btn
            :loading="creating"
            @click="create"
          >
            Create a new Game
          </v-btn>
        </v-layout>
      </v-flex>

      <v-flex
          mb-5
          xs12
        >
          <v-layout justify-center>

                    <v-text-field
                      outline
                      required
                      label="Enter Join Token"
                      :rules="requiredRules"
                      v-model="joinToken"
                      style="max-width:400px;"
                    ></v-text-field>

          </v-layout>
          <v-layout justify-center>
            <v-btn
                :loading="joining"
                @click="join"
            >Join</v-btn>
            </v-layout>


        </v-flex>


    </v-layout>
    </v-form>
  </v-container>
</template>

<script>
  import {http} from '../http'

  export default {
    data: () => ({
        joinToken: "",
        requiredRules: [
            v => !!v || 'This field is required'
        ],
    }),
    methods: {
        create: function () {
            this.$data.error=null;
            this.$data.creating=true;
            http.post("/create")
                .then(response => {
                    this.$data.response=response;
                    this.$data.creating=false;
                    this.$router.push("/play");
                })
                .catch(e => {
                    this.$data.creating=false;
                    this.$data.error=e;
                })
        },
        join: function () {
            this.$data.error=null;
            this.$data.joining=true;
            if (this.$refs.form.validate()) {
                http.put("/join/"+this.$data.joinToken)
                    .then(response => {
                        this.$data.response=response;
                        this.$data.joining=false;
                        this.$router.push("/play");
                    })
                    .catch(e => {
                        this.$data.joining=false;
                        this.$data.error=e;
                    })
            }
        }
    }
  }
</script>

<style>

</style>
