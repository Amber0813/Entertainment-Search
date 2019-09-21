import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
 name: 'truncate'
})

export class TruncatePipe implements PipeTransform {

transform(value: string, args: string[]): string {
    var start = parseInt(args[0]);
    var end = parseInt(args[1]);
    if (value.length > 35) {
        if (value.charAt(34) == ' ') {
            value = value.substring(0, 34) + '...';
        }
        else {
            var i;
            for (i = 34; i >= 0; i--) {
                if (value.charAt(i) == ' ') {
                    break;
                }
            }
            value = value.substring(0, i) + '...';
        }
    }
    return value;
   }
}