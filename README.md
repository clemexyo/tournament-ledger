## dream_games_case_study

All of the required features are implemented and mostly tested.

First thing I did was to decide on how to model the system.
I ended up with 5 database model entities: Country, Player, Scores, Tournament, TournamentGroup.
These entities have certain relationships with each other. For example, 1 player may have many scores. 1 Tournament may have many tournament groups. Please see the "model" folder to see every relationship defined.

The application itself is of 3 layers. Controller > Service > Repository.
Each entity consists of these 3 layers. Controllers handle the incoming HTTP requests and forwards the necessary data to appropiate service method. The service methods will handle the business logic by making use of the database through the repositories whenver needed. 
If all goes well response data is returned to the controller and controller sends the response together with a fitting HTTP status code. 
In the case of something going wrong, the service methods will throw the appropiate exception and the global exception handler will catch it. Global exception handler wraps the necessary error message together with a HTTP status code and sends it back to the client.

I tried to follow the Domain Driven Design patterns as much as I can. For example contollers can never reach the database directly, request always flows through service and then repository levels. 

I will briefly explain the endpoints I designed and some of the business logic:

**Country endpoints (/api/country)**
- Take for example the "createCountry" request:
Invoked by sending a post request to "/api/country" first maps the requestbody to a Dto called CreateCountryRequest. In this Dto the country name is being read from the requestbody as a string and controller then checks the validity of the object. If all is well then
"createCountry" method of the Country Service is called. Here if the country name already exists in the database the service method will throw a "CreateCountryException"

- getCountriesLeaderBoard request: this request takes a tournament_id from the request query and forwards it "generateCountriesLeaderBoard" method of country service. There, total score of each country is being calculated and sorted with respect to this score.
  In the case of equal scores, the country that managed to score a point latest will be higher in the list. This is achieved thanks to the "latest_update" attribute of the score entity.

- getCountryLeaderBoard request: this request takes a tournament_id this time together with a country_id from request query. Forwards the data to generateCountryLeaderBoard method of country service and there the leaderboard of a particular country is being generated.
The score contribution to the country of each player that plays for the country will be calculated and players will be listed with respect to that score. In this case of equal scores, again the player that managed to score a point latest will be higher in the list.

**Player endpoints (/api/players)**
- getPlayers request: invoked by sending a get request to the base url, return all players in a list of strings. toString() method of the Player model has been overloaded in a way to represent players in a good format.

- createPlayer request: Maps the requestbody to a Dto and later creates the player. I did not find it necessary to make the player names unique so that is not being checked.

- updateLevel request: invoked by sending a put request to the base url, maps the requestbody to a Dto and its validity is of course being checked. Later calls the updateLevel method of player service. In this method the player is being checked if it is in an active
group or not. Because if the player is in an active group, it should also score a point while moving on to the next level. If something goes wrong UpdatePlayerLevelRequestException is being thrown.

**Score endpoints (/api/reward)**
- claimReward request: the requestbody is mapped to a Dto after its validity checks the player is being found. If there's no such player then an excepiton is being thrown. After this the data is forwarded to claimReward method of player service. There it is checked if
the player is a winner or a second of its latest tournament group. If so, the reward in added to the player's coins.
Keep in mind if all of the scores of the tournament group is 0 that means nobody played the game thus nobody gets any rewards.
If all scores are 0 except a single player, then that player gets to be the winner and others do not earn any rewards.
After claiming the reward the player can enter another tournament group.

**Tournament endpoints (/api/tournament)**
- enterTournament request: invoked by sending a post request to the base url. The request is mapped to a Dto, validity checks are being made and the player finds a suitable spot in a tournament group.

**Tournament Group endpoints (/api/tournament-group)**
- getLeaderBoard request: generates the leaderboard of a particular tournament group. Returns it back to the client in string format.
- getPlayerRank request: return the rank of the player in a particular tournament group. The group can be active or not active, returns the rank anyways.



**Repositories**\
As I mentioned earlier every entity has their repository for database management. I had to write a few custom queries on top of the standarts provided by JpaRepository interface. Please see the repository folder under main.


**Some of the logic:**\
**Generating Leaderboards**
- this was probably the hardest business logic to implement because I only noticed that there can be equal scores when I was already deep into the project. Therefore I had to add an aditional attribute to the scores table, latest_update timestamp.
The leaderboards related to countries are generated in a way that a HasMap is being created which maps country name to a string which holds total score and latest update timestamp. First, each value of this map is compared with respect to score and in the case of
equal scores, being compared with respect to timestamp. I had to write my own comparator for this.
Generating leader board of a group was relatively easier because I could just map a Long value (the score of the player) the a timestamp. This means I didn't have to do much string manipulation.

 **Custom Queries**\
 Since may database has a lot of relations I had to create some custom queries and had to study some SQL to do so.

 **Task Scheduling**\
 2 tasks are scheduled: start tournament, end tournament. At midnight, start tournament is triggered which just creates a tournament instance. At 8pm "end tournament" is being triggered and it first convert is_active value of the last tournament instance to false. 
 Then triggers a few service methods in which players leave their groups (except the winner and the second because they need to send a "claimReward" request in order to get their rewards and leave their groups)

 **Putting players in groups**\
 In order to map players to groups I created two methods: 
 - assignPlayerToAvailableGroup: finds pending tournament groups and puts the player to first available
 - createGroupAndAssignPlayer: in this case player is valid and can enter the tournament however there isn't any group in which the player's country is unique. Therefore, create a new group instance and assign the player there.

**Testing**\
I created a h2 (in-memory) database for testing purposes. I focused mainly on integration tests in order to see how the application as a whole behaves. I couldn't finish testing everything because I on a deadline and couldn't find the time to test everything however
I tested all of the custom queries of my repositories with h2 database and everything seems to work. 
Keep in mind that I tested the whole application manually by literally creating a bouch of players and 5 countries and assigning the players to groups and stuff. However as I mentioned not all test case scenarios are implemented to the code.

**Final Note**\
Unlike what the project documentation says I used a PostgreSQL database instead of MySQL. I did this because render offers free online postgres databases so I thought it would be better to have an online database if you wanted to test the application manually.
I am also doing something I've never done before which is leaving the database crendentials in the code and putting it to the repo that way so that you can test the app using my database freely. 
I am also adding the postman json file that I used to test my endpoint so feel free to import it to your postman and test the application.
 
