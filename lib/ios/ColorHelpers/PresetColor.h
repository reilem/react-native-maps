//
//  PresetColor.h
//  react-native-google-maps
//
//  Created by Reinert Lemmens on 08/08/2018.
//

#import <Foundation/Foundation.h>
#import "SimpleColor.h"

@interface PresetColor : NSObject

@property (nonatomic) float percent;
@property (nonatomic) SimpleColor *color;

+(PresetColor *)makeWithPercent:(float)percent color:(SimpleColor *)color;

@end
