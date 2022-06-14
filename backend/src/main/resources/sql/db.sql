-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Feb 06, 2022 at 07:38 AM
-- Server version: 10.4.22-MariaDB
-- PHP Version: 8.1.2

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `timesheet`
--
CREATE DATABASE IF NOT EXISTS timesheet;
CREATE USER IF NOT EXISTS 'admin'@'localhost' IDENTIFIED BY 'pass';
CREATE USER IF NOT EXISTS 'admin'@'%' IDENTIFIED BY 'pass';
GRANT ALL ON timesheet.* TO 'admin'@'localhost';
GRANT ALL ON timesheet.* TO 'admin'@'%';

USE timesheet;

DROP TABLE IF EXISTS weeklyreport;
DROP TABLE IF EXISTS employeeprojectpackage;
DROP TABLE IF EXISTS timesheetrow;
DROP TABLE IF EXISTS timesheet;
DROP TABLE IF EXISTS monthlyreport;
DROP TABLE IF EXISTS projectpackage;
DROP TABLE IF EXISTS credential;
DROP TABLE IF EXISTS employee;
DROP TABLE IF EXISTS paygrade;


-- --------------------------------------------------------

--
-- Database: `timesheet`
--

-- --------------------------------------------------------

--
-- Table structure for table `credential`
--

CREATE TABLE `credential` (
                              `userName` varchar(20) NOT NULL,
                              `employeeNum` int(5) NOT NULL,
                              `password` varchar(30) NOT NULL,
                              `token` varchar(30) DEFAULT NULL,
                              `timestamp` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `credential`
--

INSERT INTO `credential` (`userName`, `employeeNum`, `password`, `token`, `timestamp`) VALUES
                                                                                           ('admin', 1, 'pass', 'kPb2HUOoUn6gbrLu-lJbbg==', '2022-03-30 19:47:59'),
                                                                                           ('halimb', 12, 'pass', NULL, NULL),
                                                                                           ('kimj', 11, 'pass', NULL, NULL),
                                                                                           ('leej', 7, 'pass', NULL, NULL),
                                                                                           ('lek', 3, 'pass', NULL, NULL),
                                                                                           ('liuh', 2, 'pass', 'yUbMtOP7jn3kNtrjG2Bb_Q==', '2022-03-30 22:45:02'),
                                                                                           ('panj', 8, 'pass', 'wFrFJi5XNmoWUku4zh0TjA==', '2022-03-29 16:57:23'),
                                                                                           ('parkg', 4, 'pass', NULL, NULL),
                                                                                           ('tamair', 9, 'pass', 'mxaQkCY85jkSsrZee-BFNg==', '2022-03-30 23:38:32'),
                                                                                           ('tane', 10, 'pass', NULL, NULL),
                                                                                           ('wul', 6, 'pass', NULL, NULL),
                                                                                           ('xul', 5, 'pass', NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `employee`
--

CREATE TABLE `employee` (
                            `employeeNum` int(11) NOT NULL,
                            `employeeId` varchar(20) NOT NULL,
                            `firstName` varchar(20) NOT NULL,
                            `lastName` varchar(20) NOT NULL,
                            `paygradeId` int(11) DEFAULT NULL,
                            `supervisorId` int(11) DEFAULT NULL,
                            `timesheetApproverId` int(11) DEFAULT NULL,
                            `isDisabled` tinyint(1) DEFAULT 0,
                            `role` varchar(20) DEFAULT 'Employee',
                            `flexTime` decimal(10,2) DEFAULT 20.00,
                            `vacationTime` decimal(10,2) DEFAULT 0.00
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `employee`
--

INSERT INTO `employee` (`employeeNum`, `employeeId`, `firstName`, `lastName`, `paygradeId`, `supervisorId`, `timesheetApproverId`, `isDisabled`, `role`, `flexTime`, `vacationTime`) VALUES
                                                                                                                                                                                         (1, 'A00', 'Bruce', 'Link', 1, NULL, NULL, 0, 'Administrator', '20.00', '0.00'),
                                                                                                                                                                                         (2, 'A01', 'James', 'Liu', 1, 1, 1, 0, 'Human Resource', '20.00', NULL),
                                                                                                                                                                                         (3, 'A02', 'Katherine', 'Le', 1, 1, 1, 0, 'Employee', '20.00', NULL),
                                                                                                                                                                                         (4, 'A03', 'Genie', 'Park', 1, 1, 1, 0, 'Employee', '20.00', NULL),
                                                                                                                                                                                         (5, 'A04', 'Lillian', 'Xu', 1, 1, 1, 0, 'Employee', '20.00', NULL),
                                                                                                                                                                                         (6, 'A05', 'Leon', 'Wu', 1, 1, 1, 0, 'Employee', '20.00', NULL),
                                                                                                                                                                                         (7, 'A06', 'Jay', 'Lee', 1, 1, 1, 0, 'Employee', '20.00', NULL),
                                                                                                                                                                                         (8, 'A07', 'Jason', 'Pan', 1, 1, 1, 0, 'Employee', '20.00', NULL),
                                                                                                                                                                                         (9, 'A08', 'Reo', 'Tamai', 1, 1, 1, 0, 'Employee', '20.00', NULL),
                                                                                                                                                                                         (10, 'A09', 'Eric', 'Tan', 1, 1, 1, 0, 'Employee', '20.00', NULL),
                                                                                                                                                                                         (11, 'A10', 'Joon', 'Kim', 1, 1, 1, 0, 'Employee', '20.00', NULL),
                                                                                                                                                                                         (12, 'A11', 'Ben', 'Halim', 1, 1, 1, 0, 'Employee', '20.00', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `employeeprojectpackage`
--

CREATE TABLE `employeeprojectpackage` (
                                          `employeeNum` int(11) NOT NULL,
                                          `projectPackageNum` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `employeeprojectpackage`
--

INSERT INTO `employeeprojectpackage` (`employeeNum`, `projectPackageNum`) VALUES
                                                                              (5, 8),
                                                                              (11, 8),
                                                                              (2, 8),
                                                                              (4, 8),
                                                                              (9, 8),
                                                                              (10, 8),
                                                                              (3, 8),
                                                                              (6, 8),
                                                                              (7, 8),
                                                                              (1, 8),
                                                                              (8, 8),
                                                                              (12, 8),
                                                                              (8, 9),
                                                                              (9, 9),
                                                                              (11, 10),
                                                                              (10, 10);

-- --------------------------------------------------------

--
-- Table structure for table `paygrade`
--

CREATE TABLE `paygrade` (
                            `paygradeId` int(11) NOT NULL,
                            `grade` varchar(10) NOT NULL,
                            `rate` decimal(5,2) NOT NULL,
                            `year` varchar(4) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `paygrade`
--

INSERT INTO `paygrade` (`paygradeId`, `grade`, `rate`, `year`) VALUES
                                                                   (1, 'DS', '16.00', '2022'),
                                                                   (2, 'JS', '15.50', '2022'),
                                                                   (3, 'P1', '16.50', '2022'),
                                                                   (4, 'P2', '17.00', '2022'),
                                                                   (5, 'P3', '17.50', '2022'),
                                                                   (6, 'P4', '18.00', '2022'),
                                                                   (7, 'P5', '18.50', '2022'),
                                                                   (8, 'P6', '19.00', '2022');

-- --------------------------------------------------------

--
-- Table structure for table `projectpackage`
--

CREATE TABLE `projectpackage` (
                                  `projectPackageNum` int(11) NOT NULL,
                                  `projectId` varchar(20) NOT NULL,
                                  `workPackageId` varchar(20) NOT NULL,
                                  `isOpen` tinyint(1) NOT NULL DEFAULT 1,
                                  `managerNum` int(11) NOT NULL DEFAULT 1,
                                  `assistantNum` int(11) DEFAULT NULL,
                                  `parentProjectPackageNum` int(11) DEFAULT NULL,
                                  `personDays` text DEFAULT NULL,
                                  `createdOn` date NOT NULL DEFAULT (CURRENT_DATE)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `projectpackage`
--

INSERT INTO `projectpackage` (`projectPackageNum`, `projectId`, `workPackageId`, `isOpen`, `managerNum`, `assistantNum`, `parentProjectPackageNum`, `personDays`, `createdOn`) VALUES
                                                                                                                                                                                   (0, 'SICK', 'SICK', 1, 1, NULL, NULL, NULL, '2022-02-28'),
                                                                                                                                                                                   (1, 'VACATION', 'VACATION', 1, 1, NULL, NULL, NULL, '2022-02-28'),
                                                                                                                                                                                   (8, 'project1', 'project1', 1, 2, NULL, NULL, NULL, '2022-01-30'),
                                                                                                                                                                                   (9, 'project1', 'backend', 1, 9, 8, 8, '{P1=5, DS=5}', '2022-02-28'),
                                                                                                                                                                                   (10, 'project1', 'frontend', 1, 11, 10, 8, NULL, '2022-02-28');

-- --------------------------------------------------------

--
-- Table structure for table `timesheet`
--

CREATE TABLE `timesheet` (
                             `employeeNum` int(11) NOT NULL,
                             `weekEnding` date NOT NULL,
                             `signature` tinyint(4) DEFAULT 0,
                             `isApproved` tinyint(1) DEFAULT 0,
                             `flexTime` decimal(10,2) DEFAULT 0.00
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `timesheet`
--

INSERT INTO `timesheet` (`employeeNum`, `weekEnding`, `signature`, `isApproved`, `flexTime`) VALUES
                                                                                                 (9, '2022-03-06', NULL, 1, NULL),
                                                                                                 (9, '2022-03-13', NULL, 1, NULL),
                                                                                                 (9, '2022-03-20', NULL, 1, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `timesheetrow`
--

CREATE TABLE `timesheetrow` (
                                `employeeNum` int(11) NOT NULL,
                                `weekEnding` date NOT NULL,
                                `projectPackageNum` int(11) NOT NULL,
                                `mon` decimal(4,2) NOT NULL DEFAULT 0.00,
                                `tue` decimal(4,2) NOT NULL DEFAULT 0.00,
                                `wed` decimal(4,2) NOT NULL DEFAULT 0.00,
                                `thu` decimal(4,2) NOT NULL DEFAULT 0.00,
                                `fri` decimal(4,2) NOT NULL DEFAULT 0.00,
                                `sat` decimal(4,2) NOT NULL DEFAULT 0.00,
                                `sun` decimal(4,2) NOT NULL DEFAULT 0.00
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `timesheetrow`
--

INSERT INTO `timesheetrow` (`employeeNum`, `weekEnding`, `projectPackageNum`, `mon`, `tue`, `wed`, `thu`, `fri`, `sat`, `sun`) VALUES
                                                                                                                                   (9, '2022-03-06', 9, '7.00', '7.00', '7.00', '7.00', '7.00', '2.00', '2.00'),
                                                                                                                                   (9, '2022-03-13', 9, '8.00', '8.00', '8.00', '8.00', '8.00', '0.00', '0.00'),
                                                                                                                                   (9, '2022-03-20', 9, '7.00', '7.00', '7.00', '7.00', '7.00', '2.00', '2.00');

-- --------------------------------------------------------

--
-- Table structure for table `weeklyreport`
--

CREATE TABLE `weeklyreport` (
                                `reportId` int(11) NOT NULL,
                                `projectPackageNum` int(11) NOT NULL,
                                `createdOn` date NOT NULL,
                                `createdBy` int(11) NOT NULL,
                                `reportDate` date NOT NULL,
                                `comment` text NOT NULL,
                                `workAccomplished` text NOT NULL,
                                `workPlanned` text NOT NULL,
                                `problemFaced` text NOT NULL,
                                `problemAnticipated` text NOT NULL,
                                `engineerPlanned` text DEFAULT NULL,
                                `etc` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Dumping data for table `weeklyreport`
--

INSERT INTO `weeklyreport` (`reportId`, `projectPackageNum`, `createdOn`, `createdBy`, `reportDate`, `comment`, `workAccomplished`, `workPlanned`, `problemFaced`, `problemAnticipated`, `engineerPlanned`, `etc`) VALUES
    (8, 9, '2022-03-30', 1, '2022-02-20', 'this is comments', 'this is workAccomplished', 'this is workPlanned', 'this is problemsFaced', 'this is problemsAnticipated', '{DS=5, P1=5}', '{DS=2, P1=2}');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `credential`
--
ALTER TABLE `credential`
    ADD PRIMARY KEY (`userName`),
    ADD UNIQUE KEY `employeeNum` (`employeeNum`);

--
-- Indexes for table `employee`
--
ALTER TABLE `employee`
    ADD PRIMARY KEY (`employeeNum`),
    ADD UNIQUE KEY `employeeId` (`employeeId`),
    ADD KEY `paygradeId` (`paygradeId`),
    ADD KEY `supervisorId` (`supervisorId`),
    ADD KEY `employee_ibfk_3` (`timesheetApproverId`);

--
-- Indexes for table `employeeprojectpackage`
--
ALTER TABLE `employeeprojectpackage`
    ADD KEY `employeeNum` (`employeeNum`),
    ADD KEY `employeeprojectpackage_ibfk_2` (`projectPackageNum`);

--
-- Indexes for table `paygrade`
--
ALTER TABLE `paygrade`
    ADD PRIMARY KEY (`paygradeId`),
    ADD UNIQUE KEY `unique_composite` (`year`,`grade`);

--
-- Indexes for table `projectpackage`
--
ALTER TABLE `projectpackage`
    ADD PRIMARY KEY (`projectPackageNum`),
    ADD UNIQUE KEY `unique_composite` (`projectId`,`workPackageId`),
    ADD KEY `fk1` (`parentProjectPackageNum`),
    ADD KEY `fk2` (`managerNum`),
    ADD KEY `fk3` (`assistantNum`);

--
-- Indexes for table `timesheet`
--
ALTER TABLE `timesheet`
    ADD PRIMARY KEY (`employeeNum`,`weekEnding`);

--
-- Indexes for table `timesheetrow`
--
ALTER TABLE `timesheetrow`
    ADD PRIMARY KEY (`employeeNum`,`weekEnding`,`projectPackageNum`) USING BTREE,
    ADD KEY `projectPackageNum` (`projectPackageNum`);

--
-- Indexes for table `weeklyreport`
--
ALTER TABLE `weeklyreport`
    ADD PRIMARY KEY (`reportId`),
    ADD UNIQUE KEY `workPackageNum` (`projectPackageNum`,`reportDate`),
    ADD KEY `monthly_fk2` (`createdBy`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `employee`
--
ALTER TABLE `employee`
    MODIFY `employeeNum` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- AUTO_INCREMENT for table `paygrade`
--
ALTER TABLE `paygrade`
    MODIFY `paygradeId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `projectpackage`
--
ALTER TABLE `projectpackage`
    MODIFY `projectPackageNum` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT for table `weeklyreport`
--
ALTER TABLE `weeklyreport`
    MODIFY `reportId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `credential`
--
ALTER TABLE `credential`
    ADD CONSTRAINT `credential_ibfk_1` FOREIGN KEY (`employeeNum`) REFERENCES `employee` (`employeeNum`);

--
-- Constraints for table `employee`
--
ALTER TABLE `employee`
    ADD CONSTRAINT `employee_ibfk_1` FOREIGN KEY (`paygradeId`) REFERENCES `paygrade` (`paygradeId`),
    ADD CONSTRAINT `employee_ibfk_2` FOREIGN KEY (`supervisorId`) REFERENCES `employee` (`employeeNum`),
    ADD CONSTRAINT `employee_ibfk_3` FOREIGN KEY (`timesheetApproverId`) REFERENCES `employee` (`employeeNum`);

--
-- Constraints for table `employeeprojectpackage`
--
ALTER TABLE `employeeprojectpackage`
    ADD CONSTRAINT `employeeprojectpackage_ibfk_1` FOREIGN KEY (`employeeNum`) REFERENCES `employee` (`employeeNum`),
    ADD CONSTRAINT `employeeprojectpackage_ibfk_2` FOREIGN KEY (`projectPackageNum`) REFERENCES `projectpackage` (`projectPackageNum`);

--
-- Constraints for table `projectpackage`
--
ALTER TABLE `projectpackage`
    ADD CONSTRAINT `fk1` FOREIGN KEY (`parentProjectPackageNum`) REFERENCES `projectpackage` (`projectPackageNum`),
    ADD CONSTRAINT `fk2` FOREIGN KEY (`managerNum`) REFERENCES `employee` (`employeeNum`),
    ADD CONSTRAINT `fk3` FOREIGN KEY (`assistantNum`) REFERENCES `employee` (`employeeNum`);

--
-- Constraints for table `timesheet`
--
ALTER TABLE `timesheet`
    ADD CONSTRAINT `timesheet_ibfk_1` FOREIGN KEY (`employeeNum`) REFERENCES `employee` (`employeeNum`);

--
-- Constraints for table `timesheetrow`
--
ALTER TABLE `timesheetrow`
    ADD CONSTRAINT `timesheetrow_ibfk_1` FOREIGN KEY (`employeeNum`,`weekEnding`) REFERENCES `timesheet` (`employeeNum`, `weekEnding`),
    ADD CONSTRAINT `timesheetrow_ibfk_2` FOREIGN KEY (`projectPackageNum`) REFERENCES `projectpackage` (`projectPackageNum`);

--
-- Constraints for table `weeklyreport`
--
ALTER TABLE `weeklyreport`
    ADD CONSTRAINT `monthly_fk1` FOREIGN KEY (`projectPackageNum`) REFERENCES `projectpackage` (`projectPackageNum`),
    ADD CONSTRAINT `monthly_fk2` FOREIGN KEY (`createdBy`) REFERENCES `employee` (`employeeNum`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
