A = LOAD '$INPUT' USING JsonLoader('temp:double, x:double, y:double, z:double, model:chararray');
TA = FOREACH A GENERATE temp,model;
B = FILTER TA BY (temp>=33.1);
CD = GROUP B BY model;
D = FOREACH CD GENERATE $0,SIZE($1);
store D into '$OUTPUT' using PigStorage();