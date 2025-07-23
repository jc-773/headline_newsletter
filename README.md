I like to get quick and concise news headlines about things I care about, so I created this tool that runs a cron job every morning to send me info about what's going on in the world.

Cron Job -> default prompt sent to openAI prompt endpoint -> response passed to email service (JavaMailSender) -> response sent to my email
