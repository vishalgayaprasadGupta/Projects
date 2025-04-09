# Details store for resrvation #
create table ReservationRecord(
UserName varchar(50) not null,
PNR varchar(10) primary key ,
TrainNumber varchar(10) not null ,
TrainName varchar(100)not null,
Source varchar(50) not null,
Destination varchar(50)not null,
Class varchar(30) not null,
JourneyDate varchar(50) not null,
PassengerName varchar(100) not null,
PassengerAge int(32) not null,
PassengerGender varchar(10) not null
);
PNR,TrainNumber,TrainName,Source,Destination,Class,JourneyDate,PassengerName,PassengerAge,PassengerGender


#Train Data #
create table TrainData(
TrainNumber varchar(20),
TrainName varchar(50)
);

insert into TrainData(TrainNumber,TrainName) values('01234','SuperFast Exp'),('02345','Durnoto Exp'),('03456','Mumbai Exp'),('04567','Varanasi Exp'),('05678','Ltt Superfast Exp'),('06789','Bengluru SuperfastExp');