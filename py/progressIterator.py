
class ProgressIterator:
    __slots__ = ['__iterable', 'maxval', 'currval', '_pattern']

    def __init__(self, pattern="(%s of %s)"):
        """
        ability to print progress in iterator in your code,
        just print the ProgressIterator Object itself to print the current iteration
        
        :param pattern:
            print pattern as defined in str.format()

        credit - Or Man 2022

         https://github.com/ormn96/Util

        """
        self._pattern = pattern

    def __call__(self, iterable):
        self.__iterable = iter(iterable)
        self.currval = 0
        try:
            self.maxval = len(iterable)
        except:
            self.maxval = "Unknown"
        return self

    def __iter__(self):
        return self

    def __next__(self):
        try:
            value = next(self.__iterable)
            self.currval += 1
            return value
        except StopIteration:
            raise

    def __repr__(self):
        return self._pattern % (self.currval, self.maxval)


class ExtendedProgressIterator(ProgressIterator):
    def __next__(self):
        value = ProgressIterator.__next__(self)
        if type(value) is tuple:
            return  value +(self.__repr__(),)
        else:
            return value, self.__repr__()