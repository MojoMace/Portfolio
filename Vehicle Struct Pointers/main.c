#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "foo.h"

/**
 * This program reads input from a provided file. Then based on the type of vehicle
 * read from the file the contents below the type of vehicle are then sent to the associated
 * vehicle builder function in foo.h. This information is then formatted and/or sent to 
 * additional functions in foo.h based on variable type. The vehicles are then sorted based on year ascending and finally compiled into out.txt
 *
 * 
 * @author Austin Kirk
 * @since 4/25/22
 */


//Starter Function
int main(){
    //Variables* 
    char current[50] = "";
    char c;
    char newline = *"\n";
    struct vehicleStruct** vArray = (struct vehicleStruct**)malloc(sizeof(struct vehicleStruct*));
    int size = 0;
    //Open the file for reading as reference
    FILE *fp = fopen("TestInput.txt", "r");
    FILE *op = fopen("out.txt", "w");

    struct vehicleStruct* vIn = (struct vehicleStruct*) malloc(sizeof(struct vehicleStruct));
    
    //Outer loop, reads to the EOF
    do{
        
        c = fgetc(fp);

        //Append character to string
        strncat(current,&c,1);

        //Inner loop, reads to \n or EOF, sends to helper function
        if (c == newline){
            //Get ENUM
            int e = returnEnum(current);

            if(e < 0) {
                printf("\n%s",current);
                continue;
            }

            //use switch case to create struck instance based on enum type. Struct is then added to array for later use
            switch(e){
                case (CAR):
                   vIn = carInput(fp);
                   vArray[size] = vIn;
                   size++;
                   vArray = (struct vehicleStruct**) realloc(vArray, size*sizeof(struct vehicleStruct*));
                    break;
                case (BOAT):
                    vIn = boatInput(fp);
                    vArray[size] = vIn;
                    size++;
                    vArray = (struct vehicleStruct**) realloc(vArray, size*sizeof(struct vehicleStruct*)); 
                    break;
                case (TRUCK):
                    vIn = truckInput(fp);
                    vArray[size] = vIn;
                    size++;
                    vArray = (struct vehicleStruct**) realloc(vArray, size*sizeof(struct vehicleStruct*)); 
                    break;
            }

            //Reset current
            strcpy(current,"");
        }
    } while (c != EOF);
    //call to bubble sort to sort the array of structs based on their model year in ascending order
    bubbleSort(vArray, size);

    //Loop utilized to check struct stored in array based on enum type. Once found switch case is used to send the struct in Array to formatted output 
    for(int i = 0;i < size; i++){
        int e = vArray[i]->type;
        
        switch (e){
            case(CAR):
                carOut(op, vArray[i]);
                break;
            case(BOAT):
                boatOut(op, vArray[i]);
                break;
            case(TRUCK):
                truckOut(op, vArray[i]);
                break;
        }
    }
    //Loop for freeing each individual struct within array
    for(int i = 0; i < size; i++){
        free(vArray[i]);
    }
    free(vArray);
    fclose(fp);
    fclose(op);

    return 0;
}
