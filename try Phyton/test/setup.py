from threading import Timer
import threading
import time
import odoorpc

#   Paramètres à fournir au simulateur
numberOfCompagny = 5;
price = 2.00;
needs = 10;
renewPerDay = 1000;
elasticPercent = 20;
odooAccount = 'admin';
odooPassword = 'admin';
numberOfRounds = 4;

#odoo = odoorpc.ODOO('edu-amc.odoo.com', protocol='jsonrpc+ssl', port=443);
odoo = odoorpc.ODOO('192.168.1.46',port=8069)
print(odoo.db.list());

