#ifndef FOO_DOT_H    /* This is an "include guard" */
#define FOO_DOT_H
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/**
 * This file is associated with the main project file Project4. This file contains a set
 * of utility functions that take in input read from the target file and performs a set of 
 * functions. The following functions include inputting integers, inputting float values,
 * inputting strings, and removing end of line characters. These functions are then called
 * in the vehicle building functions: boatInput, carInput, TruckInput.
 *
 * 
 * @author Austin Kirk
 * @since 4/25/22
 */

//Declare Enums
enum vehicle{CAR, TRUCK, BOAT};

struct truck{
    int numDoors;
    float towCapacity;
};
struct car{
    int numDoors;
    char* rearConfig;
};
struct boat{
    char* Motor;
};

//General vehicle structure with implemented union of specific vehicle types
struct vehicleStruct{
    char* Make;
    char* Model;
    int Year;
    char* VIN;
    enum vehicle type; 
    union {
        struct truck tck;
        struct car cars;
        struct boat boats;
    };    
};

//Takes in string input read from file and removes end of line characters
extern int RemoveEndOfLineMarker(char TheString[]){
    TheString[strcspn(TheString,"\n")] = 0;
    return 1;
}

//Takes input from file, reads integer value, and assigns to stored variable
extern int InputInteger(FILE *inFile) {
    char* CopyString = (char*)malloc(50 * sizeof(char));
    char newline = *"\n";
    char c;

    do {
        c = fgetc(inFile);
       
        strncat(CopyString,&c,1);
        
    } while(c != newline && c != EOF);

    int i = atoll(CopyString);
   
    return i;
}

//Takes input from file, reads float value, and assigns to stored variable
extern float InputFloat(FILE *inFile) {
    
    char* CopyString = (char*)malloc(50 * sizeof(char));
    char c;

    for (int i = 0; i < 3; i++){
        c = fgetc(inFile);
        strncat(CopyString,&c,1);
    }

    float f = atof(CopyString);
    return f;
}

//Takes input from file, reads string value, and assigns to stored variable
extern int InputString(FILE *inFile, char* strIn) {

    char newline = *"\n";
    char c;
    int len = 0;

    do {
        c = fgetc(inFile);
      
        strncat(strIn,&c,1);
        
        len++;

    } while(c != newline && c != EOF);
   
    RemoveEndOfLineMarker(strIn);
    
    strcat(strIn," \0");
    return len;
}

//Boat building function for creating vehicle structure with additional boat features
extern struct vehicleStruct* boatInput(FILE *fp){
    struct vehicleStruct *vehicle = (struct vehicleStruct*) malloc(sizeof(struct vehicleStruct));
    vehicle->type = BOAT;
    vehicle->Make = (char *) malloc(50);
    vehicle->Model = (char *) malloc(50);
    vehicle->Year; 
    vehicle->VIN = (char *) malloc(50);
    vehicle->boats.Motor = (char *) malloc(50);

    int newSize = InputString(fp, vehicle->Make);
    vehicle->Make = (char *) realloc(vehicle->Make, newSize*sizeof(char));
    newSize = InputString(fp, vehicle->Model);
    vehicle->Model = (char *) realloc(vehicle->Model, newSize*sizeof(char));
    vehicle->Year = InputInteger(fp);
    newSize = InputString(fp, vehicle->VIN);
    vehicle->VIN = (char *) realloc(vehicle->VIN, newSize*sizeof(char));
    newSize = InputString(fp, vehicle->boats.Motor);
    vehicle->boats.Motor = (char *) realloc(vehicle->boats.Motor, newSize*sizeof(char));
    return vehicle;
}
//Car building function for creating vehicle structure with additional car features
extern struct vehicleStruct* carInput(FILE *fp){
    struct vehicleStruct *vehicle = (struct vehicleStruct*) malloc(sizeof(struct vehicleStruct));
    vehicle->type = CAR;
    vehicle->Make = (char *) malloc(50);
    vehicle->Model = (char *) malloc(50);
    vehicle->Year; 
    vehicle->VIN = (char *) malloc(50);
    vehicle->cars.numDoors;
    vehicle->cars.rearConfig = (char *) malloc(50);

    int newSize = InputString(fp, vehicle->Make);
    vehicle->Make = (char *) realloc(vehicle->Make, newSize*sizeof(char));
    newSize = InputString(fp,vehicle->Model);
    vehicle->Model = (char *) realloc(vehicle->Model, newSize*sizeof(char));
    vehicle->Year = InputInteger(fp);
    newSize = InputString(fp, vehicle->VIN);
    vehicle->VIN = (char *) realloc(vehicle->VIN, newSize*sizeof(char));
    vehicle->cars.numDoors = InputInteger(fp);
    newSize = InputString(fp, vehicle->cars.rearConfig);
    vehicle->cars.rearConfig = (char *) realloc(vehicle->cars.rearConfig, newSize*sizeof(char));
    return vehicle;
}
//Truck building function for creating vehicle structure with additional truck features
extern struct vehicleStruct* truckInput(FILE *fp){
    struct vehicleStruct *vehicle = (struct vehicleStruct*) malloc(sizeof(struct vehicleStruct));
    vehicle->type = TRUCK;
    vehicle->Make = (char *) malloc(50);
    vehicle->Model = (char *) malloc(50);
    vehicle->Year; 
    vehicle->VIN = (char *) malloc(50);
    vehicle->tck.numDoors;
    vehicle->tck.towCapacity;
    
    int newSize = InputString(fp, vehicle->Make);
    vehicle->Make = (char *) realloc(vehicle->Make, newSize*sizeof(char));
    newSize = InputString(fp, vehicle->Model);
    vehicle->Model = (char *) realloc(vehicle->Model, newSize*sizeof(char));
    vehicle->Year = InputInteger(fp);
    newSize = InputString(fp, vehicle->VIN);
    vehicle->VIN = (char *) realloc(vehicle->VIN, newSize*sizeof(char));
    vehicle->tck.numDoors = InputInteger(fp);
    vehicle->tck.towCapacity = InputFloat(fp);
    return vehicle;
}
//Output formatter for BOAT type
extern void boatOut(FILE *out, struct vehicleStruct* boat){
    fprintf(out, "%d %s %s \n", boat->Year, boat->Make, boat->Model);
    fprintf(out, "%s %s\n", "VIN: ", boat->VIN);
    fprintf(out, "%s %s\n\n", "Motor: ", boat->boats.Motor);
}
//Output formatter for CAR type
extern void carOut(FILE *out, struct vehicleStruct* car){
    fprintf(out, "%d %s %s\n", car->Year, car->Make, car->Model);
    fprintf(out, "%s %s\n", "VIN: ", car->VIN);
    fprintf(out, "%s %d\n", "Doors: ", car->cars.numDoors);
    fprintf(out, "%s %s\n\n", "Rear Configuration: ", car->cars.rearConfig);
}
//Output formatter for TRUCK type
extern void truckOut(FILE *out, struct vehicleStruct* truck){
    fprintf(out, "%d %s %s\n", truck->Year, truck->Make, truck->Model);
    fprintf(out, "%s %s\n", "VIN: ", truck->VIN);
    fprintf(out, "%s %d\n", "Doors: ", truck->tck.numDoors);
    fprintf(out, "%s %.1f\n\n", "Max Towing Capacity: ", truck->tck.towCapacity);
}

//Swap function used by bubble sort
extern void swap(struct vehicleStruct** a, struct vehicleStruct** b){
        struct vehicleStruct* temp = *a;
        *a = *b;
        *b = temp;
    }

//bubble sort function for sorting structs based on model year in ascending order
void bubbleSort(struct vehicleStruct** vArray, int size){
        int i, j;
        for(i = 0; i < size - 1; i++){
            for(j=0; j < size - i - 1; j++){
                if(vArray[j]->Year > vArray[j+1]->Year){
                    swap(&vArray[j], &vArray[j+1]);
                }
            }
        }
    }

//function utilized to read in vehicle type. Comparison is made and associated integer value for vehicle type is returned
int returnEnum(char* type){
    //Variables

    int c = CAR;
    int b = BOAT;
    int t = TRUCK;

    RemoveEndOfLineMarker(type);

    if (strcmp("car", type) == 0){
        return c;
    }
    else if (strcmp("boat", type) == 0){
        return b;
    }
    else if (strcmp("truck", type) == 0) {
        return t;
    }
    return -1;

}


#endif /* FOO_DOT_H */
