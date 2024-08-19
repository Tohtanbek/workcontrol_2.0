
# "WorkControl" management software

Fullstack workflow management system. Full cycle of client -> manager -> worker relations implemented.


## Features

- CRM system for managers in web
- Workflow administration interface for managers and employers in telegram
- Reactive web-store for clients  


## Tech Stack

**Server:** 
- Spring boot (web, data, security, cloud etc.)
- Postgresql + liquibase
- Rabbitmq
- Telegram api [pengrad](https://github.com/pengrad/java-telegram-bot-api?tab=readme-ov-file)
- Google drive api
**Client:** vanilla js, tabulator framework

---

**Architecture** 

three microservices: 

    1. web (backend + frontend)
    2. amqp (Rabbitmq to connect 3rd and 1st)
    3. tg (telegram bot logic)

Rabbitmq is used for backups of tg bot events and for better user expreience of uploading media in bot.
## Screenshots

![demo_1](https://github.com/Tohtanbek/workcontrol_2.0/blob/first_dev_branch/demo_1.png?raw=true)
![demo_2](https://github.com/Tohtanbek/workcontrol_2.0/blob/first_dev_branch/demo_2.png?raw=true)
![demo_3](https://github.com/Tohtanbek/workcontrol_2.0/blob/first_dev_branch/demo_3.png?raw=true)
![demo_4](https://github.com/Tohtanbek/workcontrol_2.0/blob/first_dev_branch/demo_4.png?raw=true)
![demo_5](https://github.com/Tohtanbek/workcontrol_2.0/blob/first_dev_branch/demo_5.png?raw=true)





## Authors

- [Dudkin Anton](https://github.com/Tohtanbek)


## Used By

This project is used by the following companies:

- [DeepClean Austin USA](https://deepcleanhq.com/)

