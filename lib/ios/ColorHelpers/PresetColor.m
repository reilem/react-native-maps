//
//  PresetColor.m
//  react-native-google-maps
//
//  Created by Reinert Lemmens on 08/08/2018.
//

#import "PresetColor.h"

@implementation PresetColor

-(id)initWith:(float)percent color:(SimpleColor *)color {
    self = [super init];
    if (self) {
        self.percent = percent;
        self.color = color;
    }
    return self;
}

+ (PresetColor *)makeWithPercent:(float)percent color:(SimpleColor *)color {
    PresetColor *preset = [[PresetColor alloc] init];
    if (preset) {
        preset.percent = percent;
        preset.color = color;
    }
    return preset;
}

@end
