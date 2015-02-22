# Twilio Movie Assistant (McHacks 2015)

Send SMS to trial account number 438-795-1980 with movie title and revieve the movie times in ScotiaBank theatre in Montreal.
 
###Supported commands:  
- USAGE --> Returns list of supported commands  
- LIST --> Returns a numbered list of movies at theatre  
- SHOW <movie_num> --> Returns the movie's showtimes for the day  

###Examples: 

Send: 

    USAGE  

Return:  

    Twilio Movie Assistant Usage Guide:  
    LIST: Returns a list of movies available  
    SHOW movie_num: Returns the movie's showtimes  
  

Send:  

    LIST  

Return:  

    1-American Sniper  
    2-Hot Tub Time Machine 2  
    3-Interstellar  
    4-Jupiter Ascending  
    5-Kingsman: The Secret Service  
    6-The Boy Next Door  
    7-The DUFF  
    8-The Hobbit: The Battle of the Five Armies  
    9-Seventh Son  
    10-The Wedding Ringer  
    11-WWE: Fast Lane  
  

Send:  

    SHOW 1  
Return:  

    American Sniper (2014) : 12:45 pm | 3:45 | 6:45 | 9:50  

![Image of Yaktocat](https://github.com/lhsieh814/Twilio2/blob/master/images/twilio_movie_assistant.png)
