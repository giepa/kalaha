<template>
  <v-container>

    <v-container grid-list-md text-xs-center v-if="ready" class="mt-3">
        <v-layout row wrap v-if="!game.player2">
            <v-flex xs12 d-flex>
                <div style="margin:0px auto;">
                    Waiting for someone to join the game
                    <br/>
                    Join token:
                    <br/>
                    {{game.id}}
                </p>

            </v-flex>
        </v-layout>
        <v-layout row wrap v-if="game.player2 && game.playerId != game.whosNext">
            <v-flex xs12 d-flex>
                <div style="margin:0px auto;">
                    Waiting for the other guy to make a move...
                </p>
            </v-flex>
        </v-layout>
        <v-layout row wrap>
          <v-flex xs3 d-flex>
            <v-spacer />
            <v-btn class="kalaha otherkalaha ma-0 pa-0"
                large fab
              >
              {{otherkalaha.val}}
            </v-btn>
          </v-flex>
          <v-flex xs6>

              <v-container grid-list-md text-xs-center class="ma-0 pa-0">
                  <v-layout row wrap >
                    <v-flex xs2
                        v-for="item in board"
                        :key="item.pit"
                    >
                      <v-btn class="pit ma-0 pa-0"
                        small fab
                        @click="move(item.pit)"
                      >
                      {{item.val}}
                      </v-btn>
                    </v-flex>

                  </v-layout>
            </v-container>


          </v-flex>
          <v-flex xs3 d-flex>

              <v-btn class="kalaha mykalaha ma-0 pa-0"
                  large fab
                >
                {{mykalaha.val}}
                </v-btn>
          </v-flex>
        </v-layout>
  </v-container>

  </v-container>
</template>

<style>
    button.pit {

    }
    button.kalaha {
        width:60px !important;
        max-width:60px !important;
        min-width:60px !important;
        height:60px !important;
    }


</style>

<script>
  import {http} from '../http'
  import SockJS from 'sockjs-client';

  export default {
    data: () => ({
        game: {
            id: "",
            data: [],
            playerId: "",
            player1: null,
            player2: null,
            whosNext: null,
            winner: null,
        },
        mykalaha: null,
        otherkalaha: null,
        board: [],
        ready: false
    }),
    methods: {
        move: function (pit) {
            this.$data.error=null;
            http.put("/move/"+pit)
                .then(response => {
                    this.$data.response=response;
                })
                .catch(e => {
                    this.$data.error=e;
                    if (e.response) {
                      alert(e.response.data.error);
                    }
                })
        },
        newPitItem: function(p,v){
            if(!v) v = 0;;
            var pit = {pit:0, val:0};
            pit.pit=p;
            pit.val=v;
            return pit;
        },
        update: function(game){
            this.$data.game.data = game.data;
            this.$data.game.id = game.id;
            this.$data.game.playerId = game.playerId;
            this.$data.game.player1 = game.player1;
            this.$data.game.player2 = game.player2;
            this.$data.game.whosNext = game.whosNext;
            this.$data.game.winner = game.winner;
            var b;
            if(game.playerId === game.player1){
                //player 1 board layout
                b = [ 12, 11, 10, 9, 8, 7, 0, 1, 2, 3, 4, 5];
                this.$data.mykalaha = this.newPitItem(6,game.data[6]);
                this.$data.otherkalaha = this.newPitItem(13,game.data[13]);
            } else if(game.playerId === game.player2){
                //player 2 board layout
                b = [ 5, 4, 3, 2, 1, 0, 7, 8, 9, 10, 11, 12];
                this.$data.mykalaha = this.newPitItem(13,game.data[13]);
                this.$data.otherkalaha = this.newPitItem(6,game.data[6]);
            } else {
                alert(game.playerId);
            }

            var data = [];
            for(var p in b){
                data.push(this.newPitItem(b[p],game.data[b[p]]));
            }
            this.$data.board = data;
            this.$data.ready = true;
        },
        onclose: function () {

        },
        onerror: function (e) {
          alert(e);
          this.$data.error=e;
        },
        onmessage: function (e) {
          var game = JSON.parse(e.data);
          this.update(game);
        },
        connect(){
            var p = '/poll/';
            var sock = new SockJS(p);
            sock.onclose = this.onclose;
            sock.onerror = this.onerror;
            sock.onmessage = this.onmessage;
            sock.onopen = function () {

            };
        }
    },
    mounted: function(){
        this.connect();
    }
  }
</script>
