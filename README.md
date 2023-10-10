Simple Telegram bot

Java 17 + PostgreSQL to run

Maven 3 to build

### usage notes

* **MAKE SURE TO ADD BOT TO CHANNEL ADMINS TO ENABLE FORWARDING FROM IT**

### build
`mvn package`

### run
Copy `application.properties` near built .jar, configure it & run with
`java -jar`

### application properties
`bot.name=tbot` - bot name
`bot.token=xxx` - token from BotFather bot
`bot.session.expire.min=60` - Minutes before search is invalidated
`bot.debug.enabled=false` - Print stacktrace to user on error
`bot.selected-locale=en` - ru/en. Translation for user messages.

`#parser.import.files=channel1_chat-export.json,C:\\Users\\user\\Desktop\\channel2_chat-export.json,/tmp/channel3_chat-export.json` - if specified on launch, will parse posts from these channels and will add them to list of monitored channels. To obtain use Telegram Web App -> Open channel -> Export chat history -> Json format. 

### dev notes

* Use `messages_xx.properties` for all user messages.
